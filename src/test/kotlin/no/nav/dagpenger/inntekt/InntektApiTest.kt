package no.nav.dagpenger.inntekt

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InntektApiTest {

    private val env = Environment("", "", "", "", 8080)

    @Test
    fun ` should be able to Http GET isReady, isAlive and metrics endpoint `() = withTestApplication({ inntektApi(env) }) {
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
}