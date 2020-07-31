package no.nav.dagpenger.inntekt.oppslag

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import mu.KotlinLogging
import no.bekk.bekkopen.org.OrganisasjonsnummerValidator
import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.inntekt.responseObject
import no.nav.dagpenger.oidc.OidcClient
import no.nav.dagpenger.oidc.OidcToken
import java.lang.RuntimeException

private val logger = KotlinLogging.logger {}

class OppslagClient(val apiUrl: String, val oidcClient: OidcClient) : PersonOppslag {

    override suspend fun hentPerson(aktørId: String): Person? {

        val personNummer = finnNaturligIdent(aktørId)
        val navn = personNummer?.let { pnr -> personNavn(pnr) }

        return personNummer?.let { fnr ->
            navn?.let { navn ->
                Person(
                    fødselsnummer = fnr,
                    fornavn = navn.fornavn,
                    mellomnavn = navn.mellomnavn,
                    etternavn = navn.etternavn

                )
            }
        }
    }

    private val jsonAdapter = moshiInstance.adapter(PersonNameRequest::class.java)

    fun finnNaturligIdent(aktørId: String): String? {
        return withOidc { token ->
            val url = "$apiUrl/naturlig-ident"
            val (_, _, result) = with(
                url.httpGet()
                    .authentication().bearer(token.access_token)
                    .header(
                        mapOf(
                            "ident" to aktørId
                        )
                    )
            ) {
                responseObject<NaturligIdent>()
            }
            result.fold(
                { success ->
                    success.naturligIdent
                },
                { error ->
                    logger.warn(
                        "Feil ved oppslag av personnummer",
                        OppslagException(error.response.statusCode, error.message ?: "")
                    )
                    null
                }
            )
        }
    }

    fun personNavn(fødselsnummer: String): PersonNameResponse? {
        val url = "$apiUrl/person/name"

        val json = jsonAdapter.toJson(PersonNameRequest(fødselsnummer))

        return withOidc { token ->
            val (_, response, result) = with(
                url.httpPost()
                    .authentication().bearer(token.access_token)
                    .header(mapOf("Content-Type" to "application/json"))
                    .body(json)
            ) {
                responseObject<PersonNameResponse>()
            }
            result.fold(
                { success ->
                    success
                },
                { error ->
                    logger.warn(
                        "Feil ved oppslag av personnavn",
                        OppslagException(
                            response.statusCode,
                            "Response message: ${response.responseMessage}. Problem message: ${error.message}"
                        )
                    )
                    null
                }
            )
        }
    }

    fun organisasjonsNavnFor(orgNr: String): String? {
        return runCatching { Organisasjonsnummer(orgNr) }.fold(
            { organisasjonsnummer ->
                val url = "$apiUrl/organisasjon/${organisasjonsnummer.organisasjonsnummer}"
                val (_, response, result) = with(
                    url.httpGet()
                        .header(mapOf("Content-Type" to "application/json"))

                ) {
                    responseObject<Organisasjon>()
                }
                result.fold(
                    { success ->
                        success.navn
                    },
                    { error ->
                        logger.warn(
                            "Feil ved oppslag av organisasjonsnavn",
                            OppslagException(
                                response.statusCode,
                                "Response message: ${response.responseMessage}. Problem message: ${error.message}"
                            )
                        )
                        null
                    }
                )
            },
            {
                logger.warn(
                    "Feil ved oppslag av organisasjonsnavn",
                    OppslagException(
                        500,
                        "Organisasjonsnummer '$orgNr' er ikke gyldig"
                    )
                )
                null
            }
        )
    }

    private fun <T> withOidc(function: (value: OidcToken) -> T?): T? =
        runCatching { oidcClient.oidcToken() }.fold(
            function,
            onFailure = {
                logger.warn("Feil ved henting av OIDC token", OppslagException(500, it.message ?: ""))
                null
            }
        )
}

data class NaturligIdent(val naturligIdent: String)

data class Organisasjonsnummer(val organisasjonsnummer: String) {
    init {
        if (!OrganisasjonsnummerValidator.isValid(organisasjonsnummer)) throw IllegalArgumentException("Organisasjonsnummer $organisasjonsnummer er ikke gyldig")
    }
}

data class Organisasjon(val orgNr: Long, val navn: String)

class OppslagException(val statusCode: Int, override val message: String) :
    RuntimeException(message)

data class PersonNameRequest(val fødselsnummer: String)
data class PersonNameResponse(
    val etternavn: String,
    val fornavn: String,
    val mellomnavn: String,
    val sammensattNavn: String
)
