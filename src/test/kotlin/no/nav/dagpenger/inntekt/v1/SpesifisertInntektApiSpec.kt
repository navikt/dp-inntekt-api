package no.nav.dagpenger.inntekt.v1

import de.huxhorn.sulky.ulid.ULID
import io.kotlintest.matchers.string.shouldContain
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
import no.nav.dagpenger.inntekt.AuthApiKeyVerifier
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.Problem
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.ktor.auth.ApiKeyVerifier
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpesifisertInntektApiSpec {

    val validSpesifisertInntektRequestJson = """
        {
        	"aktørId": "1234",
            "vedtakId": 1,
            "beregningsDato": "2019-01-08"
        }
        """.trimIndent()

    val validJsonById = """
        {
        	"aktørId": "1234",
            "beregningsDato": "2019-01-08"
        }
        """.trimIndent()

    val jsonMissingFields = """
        {
        	"aktørId": "1234",
        }
        """.trimIndent()

    private val behandlingsInntektsGetterMock: BehandlingsInntektsGetter = mockk()
    private val inntektStoreMock: InntektStore = mockk()

    private val spesifisertInntektPath = "/v1/inntekt/spesifisert"

    private val apiKeyVerifier = ApiKeyVerifier("secret")
    private val authApiKeyVerifier = AuthApiKeyVerifier(apiKeyVerifier, listOf("test-client"))
    private val apiKey = apiKeyVerifier.generate("test-client")

    private val inntektId = InntektId(ULID().nextULID())
    private val emptyInntekt = InntektkomponentResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, "1234"))

    private val storedInntekt = StoredInntekt(
        inntektId,
        emptyInntekt,
        false
    )

    init {
        every {
            runBlocking { behandlingsInntektsGetterMock.getBehandlingsInntekt(any()) }
        } returns storedInntekt
    }

    @Test
    fun `Spesifisert inntekt API specification test - Should match json field names and formats`() = testApp {
        handleRequest(HttpMethod.Post, spesifisertInntektPath) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(validSpesifisertInntektRequestJson)
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `Fails on post request with missing fields`() = testApp {
        handleRequest(HttpMethod.Post, spesifisertInntektPath) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(jsonMissingFields)
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `Get klassifisert inntekt by ID should return 200 ok`() = testApp {
        val inntektId = InntektId(ULID().nextULID())

        every {
            runBlocking {
                inntektStoreMock.getInntekt(inntektId)
            }
        } returns StoredInntekt(
            inntektId = inntektId,
            inntekt = InntektkomponentResponse(
                arbeidsInntektMaaned = emptyList(),
                ident = Aktoer(AktoerType.AKTOER_ID, "1234")
            ),
            manueltRedigert = false,
            timestamp = LocalDateTime.now()
        )

        handleRequest(HttpMethod.Post, "$spesifisertInntektPath/${inntektId.id}") {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(validJsonById)
        }.apply {
            assertTrue(requestHandled)
            response.content.shouldContain(inntektId.id)
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `Get klassifisert inntekt by ID should verify that aktorId matched the inntekt`() = testApp {
        val inntektId = InntektId(ULID().nextULID())

        every {
            runBlocking {
                inntektStoreMock.getInntekt(inntektId)
            }
        } returns StoredInntekt(
            inntektId = inntektId,
            inntekt = InntektkomponentResponse(
                arbeidsInntektMaaned = emptyList(),
                ident = Aktoer(AktoerType.AKTOER_ID, "3245")
            ),
            manueltRedigert = false,
            timestamp = LocalDateTime.now()
        )

        handleRequest(HttpMethod.Post, "$spesifisertInntektPath/${inntektId.id}") {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(validJsonById)
        }.apply {
            assertTrue(requestHandled)
            val problem = moshiInstance.adapter<Problem>(Problem::class.java).fromJson(response.content!!)
            assertEquals("AktørId i request stemmer ikke med aktørId på inntekten du spør etter.", problem?.title)
            assertEquals("urn:dp:error:inntekt", problem?.type.toString())
            assertEquals(401, problem?.status)
            assertEquals(HttpStatusCode.Unauthorized, response.status())
        }
    }

    @Test
    fun `Get klassifisert inntekt by invalid ULID should return 400 Bad Request`() = testApp {
        handleRequest(HttpMethod.Post, "$spesifisertInntektPath/123") {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(validJsonById)
        }.apply {
            assertTrue(requestHandled)
            val problem = moshiInstance.adapter<Problem>(Problem::class.java).fromJson(response.content!!)
            assertEquals("InntektsId må være en gyldig ULID", problem?.title)
            assertEquals("urn:dp:error:inntekt", problem?.type.toString())
            assertEquals(400, problem?.status)
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `Get klassifisert inntekt by non-existing ID should return 404 Not Found`() = testApp {
        val inntektId = InntektId(ULID().nextULID())

        every {
            runBlocking {
                inntektStoreMock.getInntekt(inntektId)
            }
        } throws InntektNotFoundException("Test inntekt not found")

        handleRequest(HttpMethod.Post, "$spesifisertInntektPath/${inntektId.id}") {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(validJsonById)
        }.apply {
            assertTrue(requestHandled)
            val problem = moshiInstance.adapter<Problem>(Problem::class.java).fromJson(response.content!!)
            assertEquals("Kunne ikke finne inntekt i databasen", problem?.title)
            assertEquals("urn:dp:error:inntekt", problem?.type.toString())
            assertEquals(404, problem?.status)
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication(mockInntektApi(
            behandlingsInntektsGetter = behandlingsInntektsGetterMock,
            apiAuthApiKeyVerifier = authApiKeyVerifier,
            inntektStore = inntektStoreMock
        )) { callback() }
    }
}