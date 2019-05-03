package no.nav.dagpenger.inntekt.brreg.enhetsregisteret

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.assertTrue

class EnhetsregisteretHttpClientTest {

    val validJsonBody = """{"navn" : "NAVN"}"""

    @Rule
    @JvmField
    var wireMockRule = WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort())

    @Test
    fun `fetch organization name on 200 ok`() {
        val testOrgNr = "123456789"

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//enheter/$testOrgNr"))
                .willReturn(WireMock.aResponse().withBody(validJsonBody))
        )

        val enhetsregisteretHttpClient = EnhetsregisteretHttpClient(wireMockRule.url(""))

        val responseName = enhetsregisteretHttpClient.getOrgName(testOrgNr)

        assertEquals("NAVN", responseName)
    }

    @Test
    fun `fetch organization name on 500 server error`() {
        val testOrgNr = "123456789"

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//enheter/$testOrgNr"))
                .willReturn(
                    WireMock.serverError()
                )
        )

        val enhetsregisteretHttpClient = EnhetsregisteretHttpClient(wireMockRule.url(""))

        val result = kotlin.runCatching {
            enhetsregisteretHttpClient.getOrgName(testOrgNr)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is EnhetsregisteretHttpClientException)
        val enhetsregisteretHttpClientException = result.exceptionOrNull() as EnhetsregisteretHttpClientException
        kotlin.test.assertEquals(500, enhetsregisteretHttpClientException.status)
    }
}