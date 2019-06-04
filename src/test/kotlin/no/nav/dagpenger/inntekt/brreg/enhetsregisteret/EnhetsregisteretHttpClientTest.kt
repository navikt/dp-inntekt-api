package no.nav.dagpenger.inntekt.brreg.enhetsregisteret

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class EnhetsregisteretHttpClientTest {

    val validJsonBody = """{"navn" : "NAVN"}"""

    companion object {
        val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

        @BeforeAll
        @JvmStatic
        fun start() {
            server.start()
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            server.stop()
        }
    }

    @BeforeEach
    fun configure() {
        WireMock.configureFor(server.port())
    }

    @Test
    fun `fetch organization name on 200 ok`() {
        val testOrgNr = "123456789"

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//enheter/$testOrgNr"))
                .willReturn(WireMock.aResponse().withBody(validJsonBody))
        )

        val enhetsregisteretHttpClient = EnhetsregisteretHttpClient(server.url(""))

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

        val enhetsregisteretHttpClient = EnhetsregisteretHttpClient(server.url(""))

        val result = kotlin.runCatching {
            enhetsregisteretHttpClient.getOrgName(testOrgNr)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is EnhetsregisteretHttpClientException)
        val enhetsregisteretHttpClientException = result.exceptionOrNull() as EnhetsregisteretHttpClientException
        kotlin.test.assertEquals(500, enhetsregisteretHttpClientException.status)
    }
}