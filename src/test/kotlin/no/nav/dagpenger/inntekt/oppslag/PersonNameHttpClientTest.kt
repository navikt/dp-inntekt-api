package no.nav.dagpenger.inntekt.oppslag

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.assertTrue

class PersonNameHttpClientTest {

    val validJsonBody = """{"sammensattNavn" : "sammensattnavntest"}"""

    @Rule
    @JvmField
    var wireMockRule = WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort())

    @Test
    fun `fetch person name on 200 ok`() {
        val testFnr = "12345678912"

        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("//person/name"))
                .withRequestBody(
                    EqualToPattern(
                        """
                            {"naturligIdent":"12345678912"}
                        """.trimIndent()
                    )
                )
                .willReturn(WireMock.aResponse().withBody(validJsonBody))
        )

        val personNameHttpClient = PersonNameHttpClient(wireMockRule.url(""))

        val responseName = personNameHttpClient.getPersonName(testFnr)

        assertEquals("sammensattnavntest", responseName)
    }

    @Test
    fun `fetch person name on 500 server error`() {
        val testFnr = "12345678912"

        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("//person/name"))
                .willReturn(
                    WireMock.serverError()
                )
        )

        val personNameHttpClient = PersonNameHttpClient(wireMockRule.url(""))

        val result = kotlin.runCatching {
            personNameHttpClient.getPersonName(testFnr)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PersonNameHttpClientException)
        val personNameHttpClientException = result.exceptionOrNull() as PersonNameHttpClientException
        kotlin.test.assertEquals(500, personNameHttpClientException.status)
    }
}