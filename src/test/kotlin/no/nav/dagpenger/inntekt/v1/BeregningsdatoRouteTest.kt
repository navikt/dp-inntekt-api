package no.nav.dagpenger.inntekt.v1

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import no.nav.dagpenger.inntekt.inntektApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class BeregningsdatoRouteTest {

    @Test
    fun `get request with inntektsId parameter`() = testApp {
        handleRequest(HttpMethod.Get, "/v1/beregningsdato/12345").apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            (inntektApi(mockk(), mockk()))
        }) { callback() }
    }
}