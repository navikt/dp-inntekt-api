package no.nav.dagpenger.inntekt.brreg.enhetsregisteret

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.moshi.responseObject
import com.github.kittinunf.result.Result

class EnhetsregisteretHttpClient(private val enhentsregisteretUrl: String) {
    fun getOrgName(orgNr: String): String {

        val url = "$enhentsregisteretUrl/enheter/$orgNr"

        val (_, response, result) = with(url.httpGet()) {
            responseObject<EnhetResponse>()
        }
        return when (result) {
            is Result.Failure -> throw EnhetsregisteretHttpClientException(response.statusCode,
                "Failed to fetch organization name. Response message: ${response.responseMessage}. Problem message: ${result.error.message}"
            )
            is Result.Success -> result.get().navn
        }
    }
}

data class EnhetResponse(val navn: String)

class EnhetsregisteretHttpClientException(
    val status: Int,
    override val message: String
) : RuntimeException(message)