package no.nav.dagpenger.inntekt.oppslag

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.moshi.responseObject
import com.github.kittinunf.result.Result
import no.nav.dagpenger.inntekt.moshiInstance

class PersonNameHttpClient(private val oppslagUrl: String) {
    private val jsonAdapter = moshiInstance.adapter(PersonNameRequest::class.java)

    fun getPersonName(naturligIdent: String): String {
        val url = "$oppslagUrl/person/name"

        val json = jsonAdapter.toJson(PersonNameRequest(naturligIdent))

        val (_, response, result) = with(url.httpPost()
            .header(mapOf("Content-Type" to "application/json"))
            .body(json)) {
            responseObject<PersonNameResponse>()
        }
        return when (result) {
            is Result.Failure -> throw PersonNameHttpClientException(response.statusCode,
                "Failed to fetch name. Response message: ${response.responseMessage}. Problem message: ${result.error.message}"
            )
            is Result.Success -> result.get().sammensattNavn
        }
    }
}

data class PersonNameRequest(val naturligIdent: String)
data class PersonNameResponse(
    val etternavn: String,
    val fornavn: String,
    val mellomnavn: String,
    val sammensattNavn: String
)

class PersonNameHttpClientException(
    val status: Int,
    override val message: String
) : RuntimeException(message)