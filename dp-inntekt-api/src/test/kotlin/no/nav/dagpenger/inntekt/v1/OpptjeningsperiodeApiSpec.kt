package no.nav.dagpenger.inntekt.v1

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.inntekt.db.InntektStore
import org.junit.jupiter.api.Test

class OpptjeningsperiodeApiSpec {
    @Test
    fun `Inntjeningsperiode API specification test - Should match json field names and formats`() {

        val inntektStore: InntektStore = mockk()

        every { runBlocking {
            inntektStore.getBeregningsdato(any())
        } } returns LocalDate.of(2019, 2, 27)

        withTestApplication(mockInntektApi(inntektStore = inntektStore)) {
            handleRequest(HttpMethod.Post, "/v1/is-samme-inntjeningsperiode") {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody(
                    """
                    {
                      "aktorId": "1234",
                      "vedtakId": 5678,
                      "beregningsdato": "2019-02-27",
                      "inntektsId": "01ARZ3NDEKTSV4RRFFQ69G5FAV"
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
        """{"sammeInntjeningsPeriode":true,"parametere":{"aktorId":"1234","vedtakId":5678,"beregningsdato":"2019-02-27","inntektsId":"01ARZ3NDEKTSV4RRFFQ69G5FAV"}}"""
}
