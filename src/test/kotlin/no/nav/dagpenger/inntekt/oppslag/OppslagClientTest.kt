package no.nav.dagpenger.inntekt.oppslag

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import com.github.tomakehurst.wiremock.matching.RegexPattern
import java.util.UUID
import kotlin.test.assertTrue
import no.nav.dagpenger.oidc.OidcClient
import no.nav.dagpenger.oidc.OidcToken
import no.nav.dagpenger.oidc.StsOidcClientException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
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
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
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

    @Test
    fun `fetch person name on 200 ok`() {

        val validJsonBody = """{"sammensattNavn" : "sammensattnavntest"}"""
        val testFnr = "12345678912"

        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("//person/name"))
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
                .withRequestBody(
                    EqualToPattern(
                        """
                            {"fødselsnummer":"12345678912"}
                        """.trimIndent()
                    )
                )
                .willReturn(WireMock.aResponse().withBody(validJsonBody))
        )

        val oppslagClient = OppslagClient(server.url(""), oidcClient)

        val responseName = oppslagClient.personNavn(testFnr)

        assertEquals("sammensattnavntest", responseName)
    }

    @Test
    fun `fetch person name on 500 server error`() {

        val testFnr = "12345678912"

        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("//person/name"))
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
                .willReturn(
                    WireMock.serverError()
                )
        )

        val oppslagClient = OppslagClient(server.url(""), oidcClient)

        val result = runCatching {
            oppslagClient.personNavn(testFnr)
        }

        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `should fetch organisasjonsnavn on 200 ok`() {

        val organisasjonsnummer = "889640782"
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//organisasjon/$organisasjonsnummer"))
                .willReturn(WireMock.aResponse().withBody("""
                    {
                        "orgNr": 889640782,
                        "navn": "Arbeids- og velferdsetaten"
                    }
                    
                """.trimIndent()))
        )

        val oppslagClient = OppslagClient(server.url(""), oidcClient)

        val result = runCatching {
            oppslagClient.organisasjonsNavnFor(organisasjonsnummer)
        }

        assertTrue(result.isSuccess)
        assertEquals("Arbeids- og velferdsetaten", result.getOrNull())
    }

    @Test
    fun `fetch organisasjonsnavn  on 500 server error gives null`() {
        val organisasjonsnummer = "889640782"
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//organisasjon/$organisasjonsnummer"))
                .willReturn(
                    WireMock.serverError()
                )
        )

        val oppslagClient = OppslagClient(server.url(""), oidcClient)

        val result = runCatching {
            oppslagClient.organisasjonsNavnFor(organisasjonsnummer)
        }

        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `fetch organisasjonsnavn with illegal org nr gives null`() {
        val organisasjonsnummer = "123456789"
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("//organisasjon/$organisasjonsnummer"))
                .willReturn(
                    WireMock.serverError()
                )
        )

        val oppslagClient = OppslagClient(server.url(""), oidcClient)

        val result = runCatching {
            oppslagClient.organisasjonsNavnFor(organisasjonsnummer)
        }

        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())

        WireMock.verify(
            WireMock.exactly(0),
            WireMock.getRequestedFor(WireMock.urlEqualTo("//organisasjon/$organisasjonsnummer"))
        )
    }
}
