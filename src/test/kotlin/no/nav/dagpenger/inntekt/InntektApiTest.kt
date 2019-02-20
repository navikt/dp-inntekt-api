package no.nav.dagpenger.inntekt

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InntektApiTest {

    @Test
    fun ` should be able to Http GET isReady, isAlive and metrics endpoint `() = withTestApplication(Application::inntektApi) {
        with(handleRequest(HttpMethod.Get, "isAlive")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
        with(handleRequest(HttpMethod.Get, "isReady")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
        with(handleRequest(HttpMethod.Get, "metrics")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    /*
    @Test
    fun ` should be able to Http GET inntekt `() = withTestApplication(Application::inntektApi) {
        with(handleRequest(HttpMethod.Get, "inntekt/1234")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val inntekt = Gson().fromJson<Inntekt>(response.content, Inntekt::class.java)
            assertEquals(inntekt, Inntekt.sampleInntekt)
            assertEquals("application/json; charset=UTF-8", response.headers["Content-Type"])
        }
    }
    */
}