package no.nav.dagpenger.inntekt.oppslag

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.moshi.responseObject
import mu.KotlinLogging
import no.nav.dagpenger.oidc.OidcClient

private val logger = KotlinLogging.logger {}
class OppslagClient(val apiUrl: String, val oidcClient: OidcClient) {

    fun finnNaturligIdent(aktørId: String): String? {
        val url = "$apiUrl/naturlig-ident"
        val (_, _, result) = with(url.httpGet()
            .authentication().bearer(oidcClient.oidcToken().access_token)
            .header(
                mapOf(
                    "ident" to aktørId
                )
            )) {
            responseObject<NaturligIdent>()
        }
        return result.fold({
            success -> success.naturligIdent
        }, { error ->
            logger.warn("Feil ved oppslag av personnummer", IdentOppslagException(error.response.statusCode, error.message ?: ""))
            null
        })
    }
}

data class NaturligIdent(val naturligIdent: String)
class IdentOppslagException(val statusCode: Int, override val message: String) : Exception(message)