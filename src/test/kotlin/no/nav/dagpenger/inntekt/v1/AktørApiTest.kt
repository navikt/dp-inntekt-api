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
import no.nav.dagpenger.inntekt.ident.AktørregisterHttpClient
import no.nav.dagpenger.inntekt.oppslag.PersonNameHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
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

    private val enhetsregisteretHttpClientMock: EnhetsregisteretHttpClient = mockk()
    private val personNameHttpClientMock: PersonNameHttpClient = mockk()
    private val aktørregisterHttpClient: AktørregisterHttpClient = mockk()

    init {
        every {
            enhetsregisteretHttpClientMock.getOrgName(any())
        } returns "123456789"

        every {
            aktørregisterHttpClient.gjeldendeNorskIdent(any())
        } returns "12345678912"
    }

    @Test
    fun `name post request with good json`() = testApp {
        handleRequest(HttpMethod.Post, "/v1/aktoer/name") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(validJson)
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `name post request with bad json`() = testApp {
        handleRequest(HttpMethod.Post, "/v1/aktoer/name") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(jsonMissingFields)
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `naturlig-ident post request with correct parameters`() = testApp {
        handleRequest(HttpMethod.Get, "/v1/aktoer/naturlig-ident") {
            addHeader("ident", "6843546846411")
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `naturlig-ident get request with missing parameter`() = testApp {
        handleRequest(HttpMethod.Get, "/v1/aktoer/naturlig-ident").apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            (mockedInntektApi(
                enhetsregisteretHttpClient = enhetsregisteretHttpClientMock,
                personNameHttpClient = personNameHttpClientMock,
                aktørregisterHttpClient = aktørregisterHttpClient))
        }) { callback() }
    }
}