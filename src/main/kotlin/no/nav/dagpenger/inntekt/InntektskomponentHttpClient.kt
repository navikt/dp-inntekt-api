package no.nav.dagpenger.inntekt

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.github.kittinunf.result.Result
import no.nav.dagpenger.inntekt.oidc.OidcClient
import no.nav.dagpenger.inntekt.v1.Aktoer
import no.nav.dagpenger.inntekt.v1.AktoerType
import no.nav.dagpenger.inntekt.v1.HentInntektListeResponse
import java.time.YearMonth

class InntektskomponentHttpClient(
    private val hentInntektlisteUrl: String,
    private val oidcClient: OidcClient
) {

    fun getInntekt(fnr: String, månedFom: YearMonth, månedTom: YearMonth): HentInntektListeResponse {

        val jsonResponseAdapter = moshiInstance.adapter(HentInntektListeResponse::class.java)

        val jsonRequestRequestAdapter = moshiInstance.adapter(HentInntektListeRequest::class.java)
        val requestBody = HentInntektListeRequest(
            "DagpengerGrunnlagA-Inntekt",
            "Dagpenger",
            Aktoer(AktoerType.NATURLIG_IDENT, fnr),
            månedFom,
            månedTom
        )
        val jsonBody = jsonRequestRequestAdapter.toJson(requestBody)

        val (_, response, result) = with(hentInntektlisteUrl.httpPost()) {
            header("Authorization" to oidcClient.oidcToken().access_token.toBearerToken())
            header("Nav-Call-Id" to "dp-inntekt-api")
            body(jsonBody)
            responseObject(moshiDeserializerOf(jsonResponseAdapter))
        }
        return when (result) {
            is Result.Failure -> throw InntektskomponentenHttpClientException(
                "Failed to fetch inntekt. Response message ${response.responseMessage}",
                result.getException()
            )
            is Result.Success -> result.get()
        }
    }
}

data class HentInntektListeRequest(
    val ainntektsfilter: String,
    val formaal: String,
    val ident: Aktoer,
    val maanedFom: YearMonth,
    val maanedTom: YearMonth
)

fun String.toBearerToken() = "Bearer $this"

class InntektskomponentenHttpClientException(
    override val message: String,
    override val cause: Throwable
) : RuntimeException(message, cause)