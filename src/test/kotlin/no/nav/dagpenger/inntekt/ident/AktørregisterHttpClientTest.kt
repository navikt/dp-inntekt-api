package no.nav.dagpenger.inntekt.ident

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.dagpenger.oidc.OidcClient
import no.nav.dagpenger.oidc.OidcToken
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.util.UUID

internal class AktørregisterHttpClientTest {

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

    class DummyOidcClient : OidcClient {
        override fun oidcToken(): OidcToken = OidcToken(UUID.randomUUID().toString(), "openid", 3000)
    }

    @Test
    fun `fetch fnr on 200 ok`() {
        val testAktørId = "1234567891234"
        val testFnr = "12345678912"

        stubFor(
            get(urlEqualTo("//api/v1/identer?gjeldende=true"))
                .withHeader("Nav-Personidenter", WireMock.equalTo(testAktørId))
                .willReturn(WireMock.aResponse().withBody(validJsonBody))
        )

        val aktørregisterHttpClient =
            AktørregisterHttpClient(
                server.url(""),
                DummyOidcClient()
            )

        val responseFnr = aktørregisterHttpClient.gjeldendeNorskIdent(testAktørId)

        Assertions.assertEquals(testFnr, responseFnr)
    }

    val validJsonBody = """
        {
            "1234567891234": {
                "identer": [
                    {
                        "ident": "12345678912",
                        "identgruppe": "NorskIdent",
                        "gjeldende": true
                    },
                    {
                        "ident": "1234567891234",
                        "identgruppe": "AktoerId",
                        "gjeldende": true
                    }
                ],
                "feilmelding": null
            }
        }
        """.trimIndent()
}