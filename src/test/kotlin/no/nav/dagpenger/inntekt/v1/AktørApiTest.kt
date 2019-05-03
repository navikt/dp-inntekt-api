package no.nav.dagpenger.inntekt.v1

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.inntekt.brreg.enhetsregisteret.EnhetsregisteretHttpClient
import no.nav.dagpenger.inntekt.db.VoidInntektStore
import no.nav.dagpenger.inntekt.inntektApi
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.oppslag.PersonNameHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class AktørApiTest {
    val validJson = """
        {
            "id": "123456789",
            "aktørType": "ORGANISASJON"
        }
        """.trimIndent()

    val jsonMissingFields = """
        {
            "id": "123456789"
        }
        """.trimIndent()

    private val inntektskomponentClientMock: InntektskomponentClient = mockk()
    private val enhetsregisteretHttpClientMock: EnhetsregisteretHttpClient = mockk()
    private val personNameHttpClientMock: PersonNameHttpClient = mockk()

    init {
        every {
            enhetsregisteretHttpClientMock.getOrgName(any())
        } returns "123456789"
    }

    @Test
    fun `post request with good json`() = testApp {
        handleRequest(HttpMethod.Post, "/v1/aktoer/name") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(validJson)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `post request with bad json`() = testApp {
        handleRequest(HttpMethod.Post, "/v1/aktoer/name") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(jsonMissingFields)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            (inntektApi(
                inntektskomponentClientMock,
                VoidInntektStore(),
                enhetsregisteretHttpClientMock,
                personNameHttpClientMock))
        }) { callback() }
    }
}