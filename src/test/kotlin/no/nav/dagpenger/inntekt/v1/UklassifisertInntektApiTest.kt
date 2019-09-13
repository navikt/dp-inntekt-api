package no.nav.dagpenger.inntekt.v1

import de.huxhorn.sulky.ulid.ULID
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.shouldNotBe
import io.ktor.application.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import io.prometheus.client.CollectorRegistry
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.JwtStub
import no.nav.dagpenger.inntekt.Problem
import no.nav.dagpenger.inntekt.db.DetachedInntekt
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.ManueltRedigert
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektKlassifiseringsKoderJsonAdapter
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.mapping.GUIArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.mapping.GUIArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.mapping.GUIInntekt
import no.nav.dagpenger.inntekt.mapping.GUIInntektsKomponentResponse
import no.nav.dagpenger.inntekt.mapping.InntektMedVerdikode
import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.inntekt.oppslag.OppslagClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UklassifisertInntektApiTest {
    private val inntektskomponentClientMock: InntektskomponentClient = mockk()
    private val inntektStoreMock: InntektStore = mockk()
    private val inntektId = InntektId(ULID().nextULID())

    private val jwtStub = JwtStub("https://localhost")
    private val token = jwtStub.createTokenFor("user")

    private val notFoundBehandlingsKey =
        BehandlingsKey(aktørId = "1234", vedtakId = 1, beregningsDato = LocalDate.of(2019, 1, 8))

    private val foundBehandlingsKey =
        BehandlingsKey(aktørId = "1234", vedtakId = 1234, beregningsDato = LocalDate.of(2019, 1, 8))

    private val inntektkomponentenFoundRequest = InntektkomponentRequest(
        "1234",
        YearMonth.of(2016, 1),
        YearMonth.of(2018, 12)
    )
    private val oppslagClientMock: OppslagClient = mockk()
    private val emptyInntekt = InntektkomponentResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, "1234"))

    private val storedInntekt = StoredInntekt(
        inntektId = inntektId,
        inntekt = emptyInntekt,
        manueltRedigert = false
    )

    private val uklassifisertInntekt = "/v1/inntekt/uklassifisert"

    init {
        every {
            inntektStoreMock.getInntektId(notFoundBehandlingsKey)
        } returns null

        every {
            inntektStoreMock.getInntektId(foundBehandlingsKey)
        } returns inntektId

        every {
            inntektStoreMock.insertInntekt(foundBehandlingsKey, storedInntekt.inntekt, null, any())
        } returns storedInntekt

        every {
            inntektStoreMock.insertInntekt(
                foundBehandlingsKey,
                storedInntekt.inntekt,
                ManueltRedigert.from(true, "user"),
                any()
            )
        } returns storedInntekt

        every {
            inntektStoreMock.getInntekt(inntektId)
        } returns StoredInntekt(
            inntektId,
            InntektkomponentResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, "1234")),
            false,
            LocalDateTime.now()

        )

        every {
            runBlocking { inntektskomponentClientMock.getInntekt(inntektkomponentenFoundRequest) }
        } returns emptyInntekt

        every {
            oppslagClientMock.finnNaturligIdent(any())
        } returns "12345678912"

        every {
            oppslagClientMock.personNavn(any())
        } returns "Navn Navnesen"
    }

    @Test
    fun `GET unknown uklassifisert inntekt should return 404 not found`() = testApp {
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/${notFoundBehandlingsKey.aktørId}/${notFoundBehandlingsKey.vedtakId}/${notFoundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotFound, response.status())
            val problem = moshiInstance.adapter<Problem>(Problem::class.java).fromJson(response.content!!)
            assertEquals("Kunne ikke finne inntekt i databasen", problem?.title)
            assertEquals("urn:dp:error:inntekt", problem?.type.toString())
            assertEquals(404, problem?.status)
            assertEquals(
                "Inntekt with for InntektRequest(aktørId=1234, vedtakId=1, beregningsDato=2019-01-08) not found.",
                problem?.detail
            )
        }
    }

    @Test
    fun `GET uklassifisert without auth cookie should return 401 `() = testApp {
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/${notFoundBehandlingsKey.aktørId}/${notFoundBehandlingsKey.vedtakId}/${notFoundBehandlingsKey.beregningsDato}"
        ) {
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.Unauthorized, response.status())
            val problem = moshiInstance.adapter<Problem>(Problem::class.java).fromJson(response.content!!)
            assertEquals("Ikke innlogget", problem?.title)
            assertEquals("urn:dp:error:inntekt:auth", problem?.type.toString())
            assertEquals(401, problem?.status)
            assertEquals(
                "Cookie with name ID_token not found",
                problem?.detail
            )
        }
    }

    @Test
    fun `GET uklassifisert without inncorrect auth cookie should return  `() = testApp {

        val anotherIssuer = JwtStub("https://anotherissuer")
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/${notFoundBehandlingsKey.aktørId}/${notFoundBehandlingsKey.vedtakId}/${notFoundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.Cookie, "ID_token=${anotherIssuer.createTokenFor("user")}")
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.Unauthorized, response.status())
        }
    }

    @Test
    fun `GET uklassifisert inntekt with malformed parameters should return bad request`() = testApp {
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/blabla"
        ) {
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `Get request for uklassifisert inntekt should return 200 ok`() = testApp {
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val storedInntekt =
                moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(storedInntekt.inntektId, inntektId)
        }
    }

    @Test
    fun `Get request for uncached uklassifisert inntekt should return 200 ok`() = testApp {
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/uncached/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val uncachedInntekt =
                moshiInstance.adapter<DetachedInntekt>(DetachedInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(emptyInntekt.ident, uncachedInntekt.inntekt.ident)
        }
    }

    @Test
    fun `Post uklassifisert inntekt should return 200 ok`() = testApp {
        val guiInntekt = GUIInntekt(
            inntektId = inntektId,
            timestamp = null,
            inntekt = GUIInntektsKomponentResponse(null, null, listOf(), Aktoer(AktoerType.AKTOER_ID, "1234")),
            manueltRedigert = false,
            redigertAvSaksbehandler = false
        )

        handleRequest(
            HttpMethod.Post,
            "v1/inntekt/uklassifisert/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
            setBody(moshiInstance.adapter<GUIInntekt>(GUIInntekt::class.java).toJson(guiInntekt))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val storedInntekt =
                moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(storedInntekt.inntektId, inntektId)
        }
    }

    @Test
    fun `Post uklassifisert inntekt redigert should return 200 ok`() = testApp {
        val guiInntekt = GUIInntekt(
            inntektId = inntektId,
            timestamp = null,
            inntekt = GUIInntektsKomponentResponse(null, null, listOf(), Aktoer(AktoerType.AKTOER_ID, "1234")),
            manueltRedigert = false,
            redigertAvSaksbehandler = true
        )

        handleRequest(
            HttpMethod.Post,
            "v1/inntekt/uklassifisert/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
            setBody(moshiInstance.adapter<GUIInntekt>(GUIInntekt::class.java).toJson(guiInntekt))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val storedInntekt =
                moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(storedInntekt.inntektId, inntektId)
            shouldBeCounted(metricName = INNTEKT_KORRIGERING)
        }
    }

    @Test
    fun `Post uklassifisert inntekt med feil redigert should return 400 ok`() = testApp {
        val guiInntekt = GUIInntekt(
            inntektId = inntektId,
            timestamp = null,
            inntekt = GUIInntektsKomponentResponse(
                fraDato = null,
                tilDato = null,
                arbeidsInntektMaaned = listOf(
                    GUIArbeidsInntektMaaned(
                        aarMaaned = YearMonth.of(2019, 1),
                        avvikListe = listOf(),
                        arbeidsInntektInformasjon = GUIArbeidsInntektInformasjon(
                            inntektListe = listOf(
                                InntektMedVerdikode(
                                    beloep = BigDecimal(123),
                                    inntektskilde = "",
                                    verdikode = "Bolig",
                                    utbetaltIMaaned = YearMonth.of(2019, 1),
                                    beskrivelse = InntektBeskrivelse.BOLIG,
                                    fordel = null,
                                    inntektType = InntektType.LOENNSINNTEKT,
                                    inntektsperiodetype = null,
                                    inntektsstatus = null
                                )
                            )
                        )
                    )
                ),
                ident = Aktoer(AktoerType.AKTOER_ID, "1234")
            ),
            manueltRedigert = false,
            redigertAvSaksbehandler = true
        )

        handleRequest(
            HttpMethod.Post,
            "v1/inntekt/uklassifisert/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
            val body = moshiInstance.adapter<GUIInntekt>(GUIInntekt::class.java).toJson(guiInntekt)
            setBody(body.replace(oldValue = "123", newValue = ""))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `Post uklassifisert uncached inntekt should return 200 ok`() = testApp {

        val guiInntekt = GUIInntekt(
            inntektId = null,
            timestamp = null,
            inntekt = GUIInntektsKomponentResponse(null, null, listOf(), Aktoer(AktoerType.AKTOER_ID, "1234")),
            manueltRedigert = false,
            redigertAvSaksbehandler = true
        )

        handleRequest(
            HttpMethod.Post,
            "v1/inntekt/uklassifisert/uncached/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
            setBody(moshiInstance.adapter<GUIInntekt>(GUIInntekt::class.java).toJson(guiInntekt))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val storedInntekt =
                moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(storedInntekt.inntektId, inntektId)
            shouldBeCounted(metricName = INNTEKT_OPPFRISKING)
        }
    }

    @Test
    fun `Post uklassifisert uncached inntekt redigert should return 200 ok`() = testApp {

        val guiInntekt = GUIInntekt(
            inntektId = null,
            timestamp = null,
            inntekt = GUIInntektsKomponentResponse(null, null, listOf(), Aktoer(AktoerType.AKTOER_ID, "1234")),
            manueltRedigert = false,
            redigertAvSaksbehandler = false
        )

        handleRequest(
            HttpMethod.Post,
            "v1/inntekt/uklassifisert/uncached/${foundBehandlingsKey.aktørId}/${foundBehandlingsKey.vedtakId}/${foundBehandlingsKey.beregningsDato}"
        ) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader(HttpHeaders.Cookie, "ID_token=$token")
            setBody(moshiInstance.adapter<GUIInntekt>(GUIInntekt::class.java).toJson(guiInntekt))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val storedInntekt =
                moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(storedInntekt.inntektId, inntektId)
            shouldBeCounted(metricName = INNTEKT_OPPFRISKING_BRUKT)
        }
    }

    @Test
    fun `Should get verdikode mapping`() = testApp {
        handleRequest(HttpMethod.Get, "v1/inntekt/verdikoder") {
            addHeader(HttpHeaders.ContentType, "application/json")
        }.apply {
            assertTrue(requestHandled)
            assertEquals("application/json; charset=UTF-8", response.headers["Content-Type"])
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(runCatching { inntektKlassifiseringsKoderJsonAdapter.fromJson(response.content!!) }.isSuccess)
        }
    }

    private fun testApp(
        moduleFunction: Application.() -> Unit =
            mockInntektApi(
                inntektskomponentClient = inntektskomponentClientMock,
                inntektStore = inntektStoreMock,
                oppslagClient = oppslagClientMock,
                jwkProvider = jwtStub.stubbedJwkProvider()
            ),
        callback: TestApplicationEngine.() -> Unit
    ) {
        withTestApplication(moduleFunction) { callback() }
    }
}

private fun shouldBeCounted(metricName: String) {
    CollectorRegistry.defaultRegistry.metricFamilySamples().asSequence().find { it.name == metricName }
        ?.let { metric ->
            metric.samples[0].value shouldNotBe null
            metric.samples[0].value shouldBeGreaterThan 0.0
        }
}
