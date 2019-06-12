package no.nav.dagpenger.inntekt.inntektskomponenten.v1

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import com.github.tomakehurst.wiremock.matching.RegexPattern
import no.nav.dagpenger.oidc.OidcClient
import no.nav.dagpenger.oidc.OidcToken
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class InntektskomponentHttpClientTest {

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
        configureFor(server.port())
    }

    class DummyOidcClient : OidcClient {
        override fun oidcToken(): OidcToken = OidcToken(UUID.randomUUID().toString(), "openid", 3000)
    }

    @Test
    fun `fetch uklassifisert inntekt on 200 ok`() {
        val body = InntektskomponentHttpClientTest::class.java
            .getResource("/test-data/example-inntekt-payload.json").readText()

        stubFor(
            post(urlEqualTo("/v1/hentinntektliste"))
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
                .withHeader("Nav-Consumer-Id", EqualToPattern("dp-inntekt-api"))
                .withHeader("Nav-Call-Id", AnythingPattern())
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                )
        )

        val inntektskomponentClient = InntektskomponentHttpClient(
            server.url("/v1/hentinntektliste"),
            DummyOidcClient()
        )

        val hentInntektListeResponse =
            inntektskomponentClient.getInntekt(
                InntektkomponentRequest(
                    "",
                    YearMonth.of(2017, 3),
                    YearMonth.of(2019, 1)
                )
            )

        assertEquals("99999999999", hentInntektListeResponse.ident.identifikator)
    }

    @Test
    fun `fetch uklassifisert inntekt with spesielleinntjeningsforhold 200 ok`() {
        val body = InntektskomponentHttpClientTest::class.java
            .getResource("/test-data/example-inntekt-spesielleinntjeningsforhold.json").readText()

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/v1/hentinntektliste"))
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
                .withHeader("Nav-Consumer-Id", EqualToPattern("dp-inntekt-api"))
                .withHeader("Nav-Call-Id", AnythingPattern())
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                )
        )

        val inntektskomponentClient = InntektskomponentHttpClient(
            server.url("/v1/hentinntektliste"),
            DummyOidcClient()
        )

        val hentInntektListeResponse =
            inntektskomponentClient.getInntekt(
                InntektkomponentRequest(
                    "",
                    YearMonth.of(2017, 3),
                    YearMonth.of(2019, 1)
                )
            )

        assertEquals("8888888888", hentInntektListeResponse.ident.identifikator)
        assertEquals(
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY,
            hentInntektListeResponse.arbeidsInntektMaaned?.first()?.arbeidsInntektInformasjon?.inntektListe?.first()?.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.spesielleInntjeningsforhold
        )
    }

    @Test
    fun `fetch uklassifisert inntekt on 500 server error`() {

        stubFor(
            post(urlEqualTo("/v1/hentinntektliste"))
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
                .withHeader("Nav-Consumer-Id", EqualToPattern("dp-inntekt-api"))
                .withHeader("Nav-Call-Id", AnythingPattern())
                .willReturn(
                    WireMock.serverError()
                )
        )

        val inntektskomponentClient = InntektskomponentHttpClient(
            server.url("/v1/hentinntektliste"),
            DummyOidcClient()
        )

        val result = runCatching {
            inntektskomponentClient.getInntekt(
                InntektkomponentRequest(
                    "",
                    YearMonth.of(2017, 3),
                    YearMonth.of(2019, 1)
                )
            )
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InntektskomponentenHttpClientException)
        val inntektskomponentenHttpClientException = result.exceptionOrNull() as InntektskomponentenHttpClientException
        assertEquals(500, inntektskomponentenHttpClientException.status)
    }

    @Test
    fun `fetch uklassifisert inntekt on 500 server error with body`() {

        stubFor(
            post(urlEqualTo("/v1/hentinntektliste"))
                .withHeader("Authorization", RegexPattern("Bearer\\s[\\d|a-f]{8}-([\\d|a-f]{4}-){3}[\\d|a-f]{12}"))
                .withHeader("Nav-Consumer-Id", EqualToPattern("dp-inntekt-api"))
                .withHeader("Nav-Call-Id", AnythingPattern())
                .willReturn(
                    WireMock.serverError().withBody(errorFromInntekskomponenten)
                )
        )

        val inntektskomponentClient = InntektskomponentHttpClient(
            server.url("/v1/hentinntektliste"),
            DummyOidcClient()
        )

        val result = runCatching {
            inntektskomponentClient.getInntekt(
                InntektkomponentRequest(
                    "",
                    YearMonth.of(2017, 3),
                    YearMonth.of(2019, 1)
                )
            )
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InntektskomponentenHttpClientException)
        val inntektskomponentenHttpClientException = result.exceptionOrNull() as InntektskomponentenHttpClientException
        assertEquals(500, inntektskomponentenHttpClientException.status)
        assertEquals(
            "Failed to fetch inntekt. Response message: Server Error. Problem message: Feil i filtrering: En feil oppstod i filteret DagpengerGrunnlagA-Inntekt, Regel no.nav.inntektskomponenten.filter.regler.dagpenger.DagpengerHovedregel støtter ikke inntekter av type no.nav.inntektskomponenten.domain.Loennsinntekt",
            inntektskomponentenHttpClientException.message
        )
    }

    private val errorFromInntekskomponenten =
        """{
              "timestamp": "2019-06-12T13:59:13.233+0200",
              "status": "500",
              "error": "Internal Server Error",
              "message": "Feil i filtrering: En feil oppstod i filteret DagpengerGrunnlagA-Inntekt, Regel no.nav.inntektskomponenten.filter.regler.dagpenger.DagpengerHovedregel støtter ikke inntekter av type no.nav.inntektskomponenten.domain.Loennsinntekt",
              "path": "/hentinntektliste"
            }
        """.trimIndent()
}
