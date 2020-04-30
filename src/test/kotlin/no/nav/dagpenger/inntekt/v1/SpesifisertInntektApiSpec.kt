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
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.inntekt.AuthApiKeyVerifier
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.ktor.auth.ApiKeyVerifier
import org.junit.jupiter.api.Test

class SpesifisertInntektApiSpec {

    private val validJson = """
        {
        	"aktørId": "1234",
            "vedtakId": 1,
            "beregningsDato": "2019-01-08"
        }
        """.trimIndent()

    private val validJsonWithVedtakIdAsUlid = """
        {
            "aktørId": "1234",
             "vedtakId": "${ULID().nextULID()}",
            "beregningsDato": "2019-01-08"
        }
        """.trimIndent()

    private val jsonMissingFields = """
        {
        	"aktørId": "1234",
        }
        """.trimIndent()

    private val behandlingsInntektsGetterMock: BehandlingsInntektsGetter = mockk()

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
            setBody(validJson)
        }.apply {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `Requests without vedtakId works, and does not store data`() = testApp {
        handleRequest(HttpMethod.Post, spesifisertInntektPath) {
            addHeader(HttpHeaders.ContentType, "application/json")
            addHeader("X-API-KEY", apiKey)
            setBody(validJsonWithVedtakIdAsUlid)
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

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication(
            mockInntektApi(
                behandlingsInntektsGetter = behandlingsInntektsGetterMock,
                apiAuthApiKeyVerifier = authApiKeyVerifier
            )
        ) { callback() }
    }
}
