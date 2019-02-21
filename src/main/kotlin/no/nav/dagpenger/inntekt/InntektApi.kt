package no.nav.dagpenger.inntekt

import com.ryanharter.ktor.moshi.moshi
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.request.path
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.hotspot.DefaultExports
import no.nav.dagpenger.inntekt.oidc.StsOidcClient
import no.nav.dagpenger.inntekt.v1.InntektskomponentHttpClient
import no.nav.dagpenger.inntekt.v1.inntekt
import org.slf4j.event.Level
import java.util.concurrent.TimeUnit

fun main() {
    val env = Environment()

    DefaultExports.initialize()
    val application = embeddedServer(Netty, port = env.httpPort) {
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
        inntekt(inntektskomponentHttpClient)
        naischecks()
    }
}
