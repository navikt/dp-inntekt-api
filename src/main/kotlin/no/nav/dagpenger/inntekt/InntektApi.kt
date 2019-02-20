package no.nav.dagpenger.inntekt

import com.ryanharter.ktor.moshi.moshi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.DefaultExports
import no.nav.dagpenger.inntekt.oidc.StsOidcClient
import org.slf4j.event.Level
import java.time.YearMonth
import java.util.concurrent.TimeUnit

private val collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry

fun main() {
    DefaultExports.initialize()
    val application = embeddedServer(Netty, port = 8080) {
        inntektApi()
    }
    application.start(wait = false)
    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop(5, 60, TimeUnit.SECONDS)
    })
}

fun Application.inntektApi() {
    val env = Environment()

    val inntektskomponentHttpClient = InntektskomponentHttpClient(
        env.hentinntektListeUrl,
        StsOidcClient(env.oicdStsUrl, env.username, env.password)
    )

    install(DefaultHeaders)
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

        route("inntekt") {
            get("/{id}") {
                val aktorId = call.parameters["id"]!!

                inntektskomponentHttpClient.getInntekt(
                    aktorId,
                    YearMonth.of(2017, 1), YearMonth.of(2019, 1))

                call.respond(HttpStatusCode.OK)
            }
        }
        get("/isAlive") {
            call.respondText("ALIVE", ContentType.Text.Plain)
        }
        get("/isReady") {
            call.respondText("READY", ContentType.Text.Plain)
        }
        get("/metrics") {
            val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()
            call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
                TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
            }
        }
    }
}
