package no.nav.dagpenger.inntekt

import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.JsonEncodingException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.hotspot.DefaultExports
import mu.KotlinLogging
import no.finn.unleash.DefaultUnleash
import no.finn.unleash.util.UnleashConfig
import no.nav.dagpenger.inntekt.db.InntektIdNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.PostgresInntektStore
import no.nav.dagpenger.inntekt.db.UnleashInntektStore
import no.nav.dagpenger.inntekt.db.VoidInntektStore
import no.nav.dagpenger.inntekt.db.dataSourceFrom
import no.nav.dagpenger.inntekt.db.migrate
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentHttpClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentenHttpClientException
import no.nav.dagpenger.inntekt.oidc.StsOidcClient
import no.nav.dagpenger.inntekt.v1.inntekt
import no.nav.dagpenger.inntekt.v1.inntjeningsperiodeApi
import org.slf4j.event.Level
import java.net.URI
import java.util.concurrent.TimeUnit

private val LOGGER = KotlinLogging.logger {}

fun main() {
    val config = Configuration()

    migrate(config)

    val unleashConfig = UnleashConfig.builder()
            .appName(config.application.name)
            .unleashAPI(config.application.unleashUrl)
            .build()

    val postgresInntektStore = PostgresInntektStore(dataSourceFrom(config))
    val voidInntektStore = VoidInntektStore()
    val unleashInntektStore = UnleashInntektStore(
            postgresInntektStore = postgresInntektStore,
            voidInntektStore = voidInntektStore,
            unleash = DefaultUnleash(unleashConfig)
    )

    val inntektskomponentHttpClient = InntektskomponentHttpClient(
            config.application.hentinntektListeUrl,
            StsOidcClient(config.application.oicdStsUrl, config.application.username, config.application.password)
    )

    DefaultExports.initialize()
    val application = embeddedServer(Netty, port = config.application.httpPort) {
        inntektApi(inntektskomponentHttpClient, unleashInntektStore)
    }
    application.start(wait = false)
    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop(5, 60, TimeUnit.SECONDS)
    })
}

fun Application.inntektApi(inntektskomponentHttpClient: InntektskomponentClient, inntektStore: InntektStore) {

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
        exception<InntektIdNotFoundException> { cause ->
            LOGGER.error("Request failed!", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntekt"),
                title = "Kunne ikke finne inntekIden i databasen",
                status = 404
            )
            call.respond(HttpStatusCode.InternalServerError, error)
        }
        exception<InntektskomponentenHttpClientException> { cause ->
            LOGGER.error("Request failed against inntektskomponenten", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntektskomponenten"),
                title = "Feilet mot inntektskomponenten!",
                status = cause.status
            )
            call.respond(HttpStatusCode.fromValue(cause.status), error)
        }
        exception<JsonEncodingException> { cause ->
            LOGGER.error("Request was malformed", cause)
            val error = Problem(
                type = URI("urn:dp:error:inntekt:parameter"),
                title = "Klarte ikke å lese parameterene",
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
        inntekt(inntektskomponentHttpClient, inntektStore)
        inntjeningsperiodeApi(inntektStore)
        naischecks()
    }
}
