package no.nav.dagpenger.inntekt.v1

import com.squareup.moshi.JsonEncodingException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import no.nav.dagpenger.inntekt.DummyInntektkomponentClient
import no.nav.dagpenger.inntekt.Environment
import no.nav.dagpenger.inntekt.inntektApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

val validJson = """
{
	"aktørId": "9000000028204",
    "vedtakId": 1,
    "beregningsDato": "2019-01-08"
}
""".trimIndent()

val jsonMissingFields = """
{
	"aktørId": "9000000028204",
}
""".trimIndent()
class InntektRouteTest {

    @Test
    fun `post request with good json`() = testApp {
        handleRequest(HttpMethod.Post, "/v1/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(validJson)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `post request with bad json`() {
        assertThrows<JsonEncodingException> {
            testApp {
                handleRequest(HttpMethod.Post, "/v1/inntekt") {
                    addHeader(HttpHeaders.ContentType, "application/json")
                    setBody(jsonMissingFields)
                }
            }
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        val env = Environment("", "", "", "")
        withTestApplication({ (inntektApi(env, DummyInntektkomponentClient())) }) { callback() }
    }
}