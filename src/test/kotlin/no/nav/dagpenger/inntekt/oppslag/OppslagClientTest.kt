package no.nav.dagpenger.inntekt.oppslag

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import no.nav.dagpenger.oidc.OidcClient
import no.nav.dagpenger.oidc.OidcToken
import no.nav.dagpenger.oidc.StsOidcClientException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class OppslagClientTest {
    val validResponse = """{
        |"naturligIdent": "12345678912"
        |}""".trimMargin()

    lateinit var wiremockServer: WireMockServer
    val oidcClient = object : OidcClient {
        override fun oidcToken(): OidcToken {
            return OidcToken(UUID.randomUUID().toString(), "openid", 3000)
        }
    }

    val failingOidcClient = object : OidcClient {
        override fun oidcToken(): OidcToken {
            throw StsOidcClientException("Failed!", RuntimeException("arrgg!"))
        }
    }

    companion object {
        val server = WireMockServer(WireMockConfiguration.options().dynamicPort())

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
    fun setup() {
        WireMock.configureFor(server.port())
    }

    @Test
    fun `fetches person naturlig-ident on 200 OK`() {
        val oppslagClient = OppslagClient(server.url(""), oidcClient)
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//naturlig-ident"))
                .withHeader("ident", AnythingPattern())
                .willReturn(WireMock.aResponse().withBody(validResponse))
        )

        val pnr = oppslagClient.finnNaturligIdent("1234")
        assertEquals("12345678912", pnr)
    }

    @Test
    fun `returns empty string if something goes wrong`() {
        val oppslagClient = OppslagClient(server.url(""), oidcClient)
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//naturlig-ident"))
                .withHeader("ident", AnythingPattern())
                .willReturn(WireMock.serverError())
        )
        val pnr = oppslagClient.finnNaturligIdent("1234")
        assertNull(pnr)
    }

    @Test
    fun `returns empty string if something goes wrong with fetching OIDC token`() {
        val oppslagClient = OppslagClient(server.url(""), failingOidcClient)
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//naturlig-ident"))
                .withHeader("ident", AnythingPattern())
                .willReturn(WireMock.aResponse().withBody(validResponse))
        )
        val pnr = oppslagClient.finnNaturligIdent("1234")
        assertNull(pnr)
    }
}