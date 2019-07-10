package no.nav.dagpenger.inntekt.v1

import de.huxhorn.sulky.ulid.ULID
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
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.inntekt.JwtStub
import no.nav.dagpenger.inntekt.Problem
import no.nav.dagpenger.inntekt.db.DetachedInntekt
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.ManueltRedigert
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektApi
import no.nav.dagpenger.inntekt.inntektKlassifiseringsKoderJsonAdapter
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.mapping.GUIInntekt
import no.nav.dagpenger.inntekt.mapping.GUIInntektsKomponentResponse
import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.inntekt.oppslag.OppslagClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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

    private val notFoundRequest =
        InntektRequest(aktørId = "1234", vedtakId = 1, beregningsDato = LocalDate.of(2019, 1, 8))

    private val foundRequest =
        InntektRequest(aktørId = "1234", vedtakId = 1234, beregningsDato = LocalDate.of(2019, 1, 8))

    private val inntektkomponentenFoundRequest = InntektkomponentRequest(
        "1234",
        YearMonth.of(2016, 1),
        YearMonth.of(2018, 12)
    )
    private val oppslagClientMock: OppslagClient = mockk()
    private val emptyInntekt = InntektkomponentResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, "1234"))

    private val storedInntekt = StoredInntekt(
        inntektId,
        emptyInntekt,
        false
    )

    private val uklassifisertInntekt = "/v1/inntekt/uklassifisert"

    init {
        every {
            inntektStoreMock.getInntektId(notFoundRequest)
        } returns null

        every {
            inntektStoreMock.getInntektId(foundRequest)
        } returns inntektId

        every {
            inntektStoreMock.insertInntekt(foundRequest, storedInntekt.inntekt, null)
        } returns storedInntekt

        every {
            inntektStoreMock.insertInntekt(foundRequest, storedInntekt.inntekt, ManueltRedigert.from(true, "user"))
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
            "$uklassifisertInntekt/${notFoundRequest.aktørId}/${notFoundRequest.vedtakId}/${notFoundRequest.beregningsDato}"
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
            "$uklassifisertInntekt/${notFoundRequest.aktørId}/${notFoundRequest.vedtakId}/${notFoundRequest.beregningsDato}"
        ) {
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.Unauthorized, response.status())
            val problem = moshiInstance.adapter<Problem>(Problem::class.java).fromJson(response.content!!)
            assertEquals("Ikke innlogget", problem?.title)
            assertEquals("urn:dp:error:inntekt:auth", problem?.type.toString())
            assertEquals(401, problem?.status)
            assertEquals(
                "Cookie with name ID_Token not found",
                problem?.detail
            )
        }
    }

    @Test
    fun `GET uklassifisert without inncorrect auth cookie should return  `() = testApp {

        val anotherIssuer = JwtStub("https://anotherissuer")
        handleRequest(
            HttpMethod.Get,
            "$uklassifisertInntekt/${notFoundRequest.aktørId}/${notFoundRequest.vedtakId}/${notFoundRequest.beregningsDato}"
        ) {
            addHeader(HttpHeaders.Cookie, "ID_token=${anotherIssuer.createTokenFor("user")}")
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.Unauthorized, response.status())
        }
    }

    @Test
    fun `GET uklassifisert inntekt with malformed parameters should return bad request`() = testApp {
        handleRequest(HttpMethod.Get, "$uklassifisertInntekt/${foundRequest.aktørId}/${foundRequest.vedtakId}/blabla") {
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
            "$uklassifisertInntekt/${foundRequest.aktørId}/${foundRequest.vedtakId}/${foundRequest.beregningsDato}"
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
            "$uklassifisertInntekt/uncached/${foundRequest.aktørId}/${foundRequest.vedtakId}/${foundRequest.beregningsDato}"
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
            "v1/inntekt/uklassifisert/${foundRequest.aktørId}/${foundRequest.vedtakId}/${foundRequest.beregningsDato}"
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
            "v1/inntekt/uklassifisert/${foundRequest.aktørId}/${foundRequest.vedtakId}/${foundRequest.beregningsDato}"
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
            "v1/inntekt/uklassifisert/uncached/${foundRequest.aktørId}/${foundRequest.vedtakId}/${foundRequest.beregningsDato}"
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
            "v1/inntekt/uklassifisert/uncached/${foundRequest.aktørId}/${foundRequest.vedtakId}/${foundRequest.beregningsDato}"
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
        moduleFunction: Application.() -> Unit = {
            inntektApi(
                inntektskomponentClientMock,
                inntektStoreMock,
                oppslagClientMock,
                mockk(relaxed = true),
                jwkProvider = jwtStub.stubbedJwkProvider()
            )
        },
        callback: TestApplicationEngine.() -> Unit
    ) {
        withTestApplication(moduleFunction) { callback() }
    }
}