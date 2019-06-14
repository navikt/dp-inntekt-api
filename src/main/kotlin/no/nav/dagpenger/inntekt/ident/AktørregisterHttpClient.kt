package no.nav.dagpenger.inntekt.ident

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.oidc.OidcClient

class AktørregisterHttpClient(val baseUrl: String, private val oidcClient: OidcClient) {

    private val jsonResponseAdapter: JsonAdapter<Map<String, IdentResponse>> = moshiInstance.adapter(
        Types.newParameterizedType(Map::class.java, String::class.java, IdentResponse::class.java))

    private fun gjeldendeIdenter(ident: String): List<Ident> {

        val url = "$baseUrl/api/v1/identer?gjeldende=true"

        val (_, response, result) = with(url.httpGet()
            .authentication().bearer(oidcClient.oidcToken().access_token)
            .header(mapOf(
                "Accept" to "application/json",
                "Nav-Call-Id" to "dagpenger",
                "Nav-Consumer-Id" to "dp-inntekt-api",
                "Nav-Personidenter" to ident
            ))) {
            responseObject(moshiDeserializerOf(jsonResponseAdapter))
        }
        return when (result) {
            is Result.Failure -> throw AktørregisterHttpClientException(
                response.statusCode,
                "Failed to fetch name. Response message: ${response.responseMessage}. Problem message: ${result.error.message}"
            )
            is Result.Success -> result.get()[ident]?.identer ?: emptyList()
        }
    }

    private fun gjeldendeIdent(ident: String, gruppe: IdentGruppe): String {
        return gjeldendeIdenter(ident).first {
            it.identgruppe == gruppe
        }.ident
    }

    fun gjeldendeNorskIdent(ident: String): String {
        return gjeldendeIdent(ident, IdentGruppe.NorskIdent)
    }
}

private data class IdentResponse(
    val identer: List<Ident>,
    val feilmelding: String?
)

private enum class IdentGruppe {
    AktoerId, NorskIdent
}

private data class Ident(val ident: String, val identgruppe: IdentGruppe)

class AktørregisterHttpClientException(
    val status: Int,
    override val message: String
) : RuntimeException(message)
