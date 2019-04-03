package no.nav.dagpenger.inntekt.inntektskomponenten.v1

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.github.kittinunf.result.Result
import no.nav.dagpenger.inntekt.oidc.OidcClient

import no.nav.dagpenger.inntekt.moshiInstance
import java.time.YearMonth

class InntektskomponentHttpClient(
    private val hentInntektlisteUrl: String,
    private val oidcClient: OidcClient
) : InntektskomponentClient {

    override fun getInntekt(aktørId: String, månedFom: YearMonth, månedTom: YearMonth): InntektkomponentenResponse {

        val jsonResponseAdapter = moshiInstance.adapter(InntektkomponentenResponse::class.java)

        val jsonRequestRequestAdapter = moshiInstance.adapter(HentInntektListeRequest::class.java)
        val requestBody = HentInntektListeRequest(
                "DagpengerGrunnlagA-Inntekt",
                "Dagpenger",
                Aktoer(AktoerType.AKTOER_ID, aktørId),
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
            is Result.Failure -> throw InntektskomponentenHttpClientException(response.statusCode,
                    "Failed to fetch inntekt. Response message: ${response.responseMessage}. Problem message: ${result.error.message}"
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
    val status: Int,
    override val message: String
) : RuntimeException(message)