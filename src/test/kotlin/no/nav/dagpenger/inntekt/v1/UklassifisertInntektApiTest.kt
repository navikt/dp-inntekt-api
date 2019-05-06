package no.nav.dagpenger.inntekt.v1

import de.huxhorn.sulky.ulid.ULID
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.inntekt.Problem
import no.nav.dagpenger.inntekt.db.InntektCompoundKey
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektApi
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.moshiInstance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UklassifisertInntektApiTest {
    private val inntektskomponentClientMock: InntektskomponentClient = mockk()
    private val inntektStorMock: InntektStore = mockk()
    private val inntektId = InntektId(ULID().nextULID())

    private val reqAdapter = moshiInstance.adapter<InntektRequest>(InntektRequest::class.java)
    private val storedInntektAdapter = moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java)

    private val notFoundRequest =
        InntektRequest(aktørId = "1234", vedtakId = 1, beregningsDato = LocalDate.of(2019, 1, 8))

    private val foundRequest =
        InntektRequest(aktørId = "1234", vedtakId = 1234, beregningsDato = LocalDate.of(2019, 1, 8))

    private val storedInntekt = StoredInntekt(
        inntektId,
        InntektkomponentResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, "1234"))
    )

    private val uklassifisertInntekt = "/v1/inntekt/uklassifisert"

    init {
        every {
            inntektStorMock.getInntektId(notFoundRequest)
        } returns null

        every {
            inntektStorMock.getInntektId(foundRequest)
        } returns inntektId

        every {
            inntektStorMock.getInntektCompoundKey(inntektId)
        } returns InntektCompoundKey(foundRequest.aktørId, foundRequest.vedtakId, foundRequest.beregningsDato)

        every {
            inntektStorMock.insertInntekt(foundRequest, storedInntekt.inntekt)
        } returns storedInntekt

        every {
            inntektStorMock.getInntekt(inntektId)
        } returns StoredInntekt(inntektId, InntektkomponentResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, "1234")))
    }

    @Test
    fun `Get unknown uklassifisert inntekt should return 404 not found`() = testApp {
        handleRequest(HttpMethod.Post, uklassifisertInntekt) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(reqAdapter.toJson(notFoundRequest))
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
    fun `Get uklassifisert inntekt should return 200 ok`() = testApp {
        handleRequest(HttpMethod.Post, uklassifisertInntekt) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(reqAdapter.toJson(foundRequest))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            val storedInntekt =
                moshiInstance.adapter<StoredInntekt>(StoredInntekt::class.java).fromJson(response.content!!)!!
            assertEquals(storedInntekt.inntektId, inntektId)
        }
    }

    @Test
    fun `Post uklassifisert inntekt should return 200 ok`() = testApp {
        handleRequest(HttpMethod.Post, "v1/inntekt/uklassifisert/update") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(storedInntektAdapter.toJson(storedInntekt))
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            (inntektApi(inntektskomponentClientMock, inntektStorMock, mockk(), mockk()))
        }) { callback() }
    }
}