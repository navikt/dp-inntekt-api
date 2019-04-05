package no.nav.dagpenger.inntekt.v1

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import no.nav.dagpenger.inntekt.inntektApi
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class InntjeningsperiodeApiSpec {
    @Test
    fun `Inntjeningsperiode API specification test - Should match json field names and formats`() {

        withTestApplication({
            inntektApi(
                mockk(),
                mockk()
            )
        }) {
            handleRequest(HttpMethod.Post, "/v1/inntjeningsperiode") {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody(
                    """
                    {
                      "aktorId": "1234",
                      "vedtakId": 5678,
                      "beregningsdato": "2019-02-27",
                      "inntektsId": "12345"
                    }
                    """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(expectedJson, response.content)
            }
        }
    }

    private val expectedJson =
        """{"sammeInntjeningsPeriode":true,"parametere":{"aktorId":"1234","vedtakId":5678,"beregningsdato":"2019-02-27","inntektsId":"12345"}}"""
}