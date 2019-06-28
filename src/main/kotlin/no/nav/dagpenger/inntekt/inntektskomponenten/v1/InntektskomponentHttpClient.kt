package no.nav.dagpenger.inntekt.inntektskomponenten.v1

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.github.kittinunf.result.Result
import de.huxhorn.sulky.ulid.ULID
import io.prometheus.client.Summary
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.oidc.OidcClient
import java.time.YearMonth

private val LOGGER = KotlinLogging.logger {}
private val jsonResponseAdapter = moshiInstance.adapter(InntektkomponentResponse::class.java)
private val jsonRequestRequestAdapter = moshiInstance.adapter(HentInntektListeRequest::class.java)
private val jsonMapAdapter = moshiInstance.adapter(Map::class.java)
private val ulid = ULID()

const val INNTEKTSKOMPONENT_CLIENT_SECONDS_METRICNAME = "inntektskomponent_client_seconds"
private val clientLatencyStats: Summary = Summary.build()
    .name(INNTEKTSKOMPONENT_CLIENT_SECONDS_METRICNAME)
    .quantile(0.5, 0.05) // Add 50th percentile (= median) with 5% tolerated error
    .quantile(0.9, 0.01) // Add 90th percentile with 1% tolerated error
    .quantile(0.99, 0.001) // Add 99th percentile with 0.1% tolerated error
    .help("Latency arena-adapter regel client, in seconds")
    .register()

class InntektskomponentHttpClient(
    private val hentInntektlisteUrl: String,
    private val oidcClient: OidcClient
) : InntektskomponentClient {

    override fun getInntekt(request: InntektkomponentRequest): InntektkomponentResponse {
        LOGGER.info("Fetching new inntekt for $request")

        val requestBody = HentInntektListeRequest(
            "DagpengerGrunnlagA-Inntekt",
            "Dagpenger",
            Aktoer(AktoerType.AKTOER_ID, request.aktørId),
            request.månedFom,
            request.månedTom
        )

        val jsonBody = jsonRequestRequestAdapter.toJson(requestBody)
        val timer = clientLatencyStats.startTimer()
        try {
            val (_, response, result) = with(hentInntektlisteUrl.httpPost()) {
                authentication().bearer(oidcClient.oidcToken().access_token)
                header("Nav-Consumer-Id" to "dp-inntekt-api")
                header("Nav-Call-Id" to ulid.nextULID())
                body(jsonBody)
                responseObject(moshiDeserializerOf(jsonResponseAdapter))
            }

            return when (result) {

                is Result.Failure -> {
                    val resp = result.error.response.body().asString("application/json")
                    val message = runCatching {
                        jsonMapAdapter.fromJson(resp)
                    }.let {
                        val s = it.getOrNull()?.get("message")?.toString() ?: result.error.message
                        s
                    }
                    throw InntektskomponentenHttpClientException(
                        response.statusCode,
                        "Failed to fetch inntekt. Response message: ${response.responseMessage}. Problem message: $message"
                    )
                }

                is Result.Success -> result.get()
            }
        } finally {
            timer.observeDuration()
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

class InntektskomponentenHttpClientException(
    val status: Int,
    override val message: String
) : RuntimeException(message)