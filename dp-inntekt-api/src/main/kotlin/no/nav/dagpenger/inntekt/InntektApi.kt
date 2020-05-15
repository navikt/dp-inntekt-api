package no.nav.dagpenger.inntekt

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.isSuccess
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.micrometer.core.instrument.Clock
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot.DefaultExports
import java.net.URI
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.db.IllegalInntektIdException
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.PostgresInntektStore
import no.nav.dagpenger.inntekt.db.dataSourceFrom
import no.nav.dagpenger.inntekt.db.migrate
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentHttpClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentenHttpClientException
import no.nav.dagpenger.inntekt.oppslag.OppslagClient
import no.nav.dagpenger.inntekt.rpc.InntektGrpcServer
import no.nav.dagpenger.inntekt.subsumsjonbrukt.KafkaSubsumsjonBruktDataConsumer
import no.nav.dagpenger.inntekt.subsumsjonbrukt.Vaktmester
import no.nav.dagpenger.inntekt.v1.inntekt
import no.nav.dagpenger.inntekt.v1.opptjeningsperiodeApi
import no.nav.dagpenger.inntekt.v1.uklassifisertInntekt
import no.nav.dagpenger.ktor.auth.ApiKeyCredential
import no.nav.dagpenger.ktor.auth.ApiKeyVerifier
import no.nav.dagpenger.ktor.auth.ApiPrincipal
import no.nav.dagpenger.ktor.auth.apiKeyAuth
import no.nav.dagpenger.oidc.StsOidcClient
import org.slf4j.event.Level

private val LOGGER = KotlinLogging.logger {}
private val sikkerLogg = KotlinLogging.logger("tjenestekall")
private val config = Configuration()

fun main() = runBlocking {

    migrate(config)
    val jwkProvider = JwkProviderBuilder(URL(config.application.jwksUrl))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    val authApiKeyVerifier = AuthApiKeyVerifier(ApiKeyVerifier(config.application.apiSecret), config.application.allowedApiKeys)

    val dataSource = dataSourceFrom(config)
    val postgresInntektStore = PostgresInntektStore(dataSource)
    val stsOidcClient =
        StsOidcClient(config.application.oicdStsUrl, config.application.username, config.application.password)

    val subsumsjonBruktDataConsumer = KafkaSubsumsjonBruktDataConsumer(config, postgresInntektStore).apply {
        listen()
    }

    val gRpcServer = InntektGrpcServer(port = 50051, inntektStore = postgresInntektStore, apiKeyVerifier = authApiKeyVerifier)

    launch {
        gRpcServer.start()
        gRpcServer.blockUntilShutdown()
    }

    val vaktmester = Vaktmester(dataSource)

    fixedRateTimer(
        name = "vaktmester",
        initialDelay = TimeUnit.MINUTES.toMillis(10),
        period = TimeUnit.HOURS.toMillis(12),
        action = {
            LOGGER.info { "Vaktmesteren rydder" }
            vaktmester.rydd()
            LOGGER.info { "Vaktmesteren er ferdig... for denne gang" }
        })

    val inntektskomponentHttpClient = InntektskomponentHttpClient(
        config.application.hentinntektListeUrl,
        stsOidcClient
    )
    val oppslagClient = OppslagClient(config.application.oppslagUrl, stsOidcClient)

    val cachedInntektsGetter = BehandlingsInntektsGetter(inntektskomponentHttpClient, postgresInntektStore)

    DefaultExports.initialize()
    val application = embeddedServer(Netty, port = config.application.httpPort) {
        inntektApi(
            inntektskomponentHttpClient,
            postgresInntektStore,
            cachedInntektsGetter,
            oppslagClient,
            authApiKeyVerifier,
            jwkProvider,
            listOf(
                postgresInntektStore as HealthCheck,
                subsumsjonBruktDataConsumer as HealthCheck
            )
        )
    }.start()
    Runtime.getRuntime().addShutdownHook(Thread {
        subsumsjonBruktDataConsumer.stop()
        application.stop(5000, 60000)
    })
}

fun Application.inntektApi(
    inntektskomponentHttpClient: InntektskomponentClient,
    inntektStore: InntektStore,
    behandlingsInntektsGetter: BehandlingsInntektsGetter,
    oppslagClient: OppslagClient,
    apiAuthApiKeyVerifier: AuthApiKeyVerifier,
    jwkProvider: JwkProvider,
    healthChecks: List<HealthCheck>
) {

    install(DefaultHeaders)
    install(MicrometerMetrics) {
        registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT, CollectorRegistry.defaultRegistry, Clock.SYSTEM)
    }
    install(Authentication) {
        apiKeyAuth {
            apiKeyName = "X-API-KEY"
            validate { apikeyCredential: ApiKeyCredential ->
                when {
                    apiAuthApiKeyVerifier.verify(apikeyCredential.value) -> ApiPrincipal(apikeyCredential)
                    else -> null
                }
            }
        }

        jwt(name = "jwt") {
            realm = "dp-inntekt-api"
            verifier(jwkProvider, config.application.jwksIssuer) {
                acceptNotBefore(10)
                acceptIssuedAt(10)
            }
            authHeader { call ->
                val cookie = call.request.cookies["ID_token"]
                    ?: throw CookieNotSetException("Cookie with name ID_token not found")
                HttpAuthHeader.Single("Bearer", cookie)
            }
            validate { credentials ->
                return@validate JWTPrincipal(credentials.payload)
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            LOGGER.error("Request failed!", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntekt"),
                title = "Uhåndtert feil!"
            )
            call.respond(HttpStatusCode.InternalServerError, error)
        }
        exception<InntektNotFoundException> { cause ->
            LOGGER.warn("Request failed!", cause)
            val problem = Problem(
                type = URI("urn:dp:error:inntekt"),
                title = "Kunne ikke finne inntekt i databasen",
                status = 404,
                detail = cause.message
            )
            call.respond(HttpStatusCode.NotFound, problem)
        }
        exception<IllegalInntektIdException> { cause ->
            LOGGER.warn("Request failed!", cause)
            val problem = Problem(
                type = URI("urn:dp:error:inntekt"),
                title = "InntektsId må være en gyldig ULID",
                detail = cause.message,
                status = HttpStatusCode.BadRequest.value
            )
            call.respond(HttpStatusCode.BadRequest, problem)
        }
        exception<InntektskomponentenHttpClientException> { cause ->
            val statusCode =
                if (HttpStatusCode.fromValue(cause.status).isSuccess()) HttpStatusCode.InternalServerError else HttpStatusCode.fromValue(
                    cause.status
                )
            sikkerLogg.error(cause) { "Request failed against inntektskomponenten" }
            LOGGER.error("Request failed against inntektskomponenten")
            val error = Problem(
                type = URI("urn:dp:error:inntektskomponenten"),
                title = "Innhenting av inntekt mot a-inntekt feilet. Prøv igjen senere",
                status = statusCode.value,
                detail = cause.detail
            )
            call.respond(statusCode, error)
        }
        exception<JsonEncodingException> { cause ->
            LOGGER.warn("Request was malformed", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntekt:parameter"),
                title = "Klarte ikke å lese parameterene",
                status = 400
            )
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<JsonDataException> { cause ->
            LOGGER.warn("Request does not match expected json", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntekt:parameter"),
                title = "Parameteret er ikke gyldig, mangler obligatorisk felt: '${cause.message}'",
                status = 400
            )
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<IllegalArgumentException> { cause ->
            LOGGER.warn("Request does not match expected json", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntekt:parameter"),
                title = "Parameteret er ikke gyldig, mangler obligatorisk felt: '${cause.message}'",
                status = 400
            )
            call.respond(HttpStatusCode.BadRequest, error)
        }
        exception<CookieNotSetException> { cause ->
            LOGGER.warn("Unauthorized call", cause)
            val statusCode = HttpStatusCode.Unauthorized
            val error = Problem(
                type = URI("urn:dp:error:inntekt:auth"),
                title = "Ikke innlogget",
                detail = "${cause.message}",
                status = statusCode.value
            )
            call.respond(statusCode, error)
        }
    }
    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            !call.request.path().startsWith("/isAlive") &&
                !call.request.path().startsWith("/isReady") &&
                !call.request.path().startsWith("/metrics")
        }
    }
    install(ContentNegotiation) {
        moshi(moshiInstance)
    }

    routing {
        route("/v1") {
            route("/inntekt") {
                inntekt(behandlingsInntektsGetter)
                uklassifisertInntekt(inntektskomponentHttpClient, inntektStore, oppslagClient)
            }
            opptjeningsperiodeApi(inntektStore)
        }
        naischecks(healthChecks)
    }
}

data class AuthApiKeyVerifier(private val apiKeyVerifier: ApiKeyVerifier, private val clients: List<String>) {
    fun verify(payload: String): Boolean {
        return clients.map { apiKeyVerifier.verify(payload, it) }.firstOrNull { it } ?: false
    }
}
