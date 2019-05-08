package no.nav.dagpenger.inntekt

import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.hotspot.DefaultExports
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.brreg.enhetsregisteret.EnhetsregisteretHttpClient
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.PostgresInntektStore
import no.nav.dagpenger.inntekt.db.dataSourceFrom
import no.nav.dagpenger.inntekt.db.migrate
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentHttpClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentenHttpClientException
import no.nav.dagpenger.inntekt.oidc.StsOidcClient
import no.nav.dagpenger.inntekt.oppslag.PersonNameHttpClient
import no.nav.dagpenger.inntekt.v1.aktørApi
import no.nav.dagpenger.inntekt.v1.inntekt
import no.nav.dagpenger.inntekt.v1.inntjeningsperiodeApi
import org.slf4j.event.Level
import java.net.URI
import java.util.concurrent.TimeUnit

private val LOGGER = KotlinLogging.logger {}

fun main() {
    val config = Configuration()

    migrate(config)

    val postgresInntektStore = PostgresInntektStore(dataSourceFrom(config))

    val inntektskomponentHttpClient = InntektskomponentHttpClient(
            config.application.hentinntektListeUrl,
            StsOidcClient(config.application.oicdStsUrl, config.application.username, config.application.password)
    )

    val enhetsregisteretHttpClient = EnhetsregisteretHttpClient(config.application.enhetsregisteretUrl)

    val personNameHttpClient = PersonNameHttpClient(config.application.oppslagUrl)

    DefaultExports.initialize()
    val application = embeddedServer(Netty, port = config.application.httpPort) {
        inntektApi(inntektskomponentHttpClient, postgresInntektStore, enhetsregisteretHttpClient, personNameHttpClient)
    }
    application.start(wait = false)
    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop(5, 60, TimeUnit.SECONDS)
    })
}

fun Application.inntektApi(
    inntektskomponentHttpClient: InntektskomponentClient,
    inntektStore: InntektStore,
    enhetsregisteretHttpClient: EnhetsregisteretHttpClient,
    personNameHttpClient: PersonNameHttpClient
) {

    install(DefaultHeaders)

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
        exception<InntektskomponentenHttpClientException> { cause ->
            val statusCode = if (HttpStatusCode.fromValue(cause.status).isSuccess()) HttpStatusCode.InternalServerError else HttpStatusCode.fromValue(cause.status)
            LOGGER.error("Request failed against inntektskomponenten", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntektskomponenten"),
                title = "Feilet mot inntektskomponenten!",
                status = statusCode.value
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
            inntekt(inntektskomponentHttpClient, inntektStore)
            inntjeningsperiodeApi(inntektStore)
            aktørApi(enhetsregisteretHttpClient, personNameHttpClient)
        }
        naischecks()
    }
}
