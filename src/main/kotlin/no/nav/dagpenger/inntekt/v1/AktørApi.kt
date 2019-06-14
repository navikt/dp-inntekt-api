package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.CacheControl
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.cacheControl
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.brreg.enhetsregisteret.EnhetsregisteretHttpClient
import no.nav.dagpenger.inntekt.ident.AktørregisterHttpClient
import no.nav.dagpenger.inntekt.oppslag.PersonNameHttpClient

fun Route.aktørApi(
    enhetsregisteretHttpClient: EnhetsregisteretHttpClient,
    personNameHttpClient: PersonNameHttpClient,
    aktørregisterHttpClient: AktørregisterHttpClient
) {

    route("aktoer/name") {
        post {
            val request = call.receive<AktørInfoRequest>()

            val name = when (request.aktørType) {
                AktørType.ORGANISASJON -> enhetsregisteretHttpClient.getOrgName(request.id)
                AktørType.NATURLIG_IDENT -> personNameHttpClient.getPersonName(request.id)
                else -> throw NotImplementedError()
            }

            val response = AktørInfoResponse(name, request.aktørType, request.id)

            val cache = CacheControl.MaxAge(maxAgeSeconds = 86400, visibility = CacheControl.Visibility.Public)
            call.response.cacheControl(cache)
            call.respond(HttpStatusCode.OK, response)
        }
    }

    route("aktoer/naturlig-ident") {
        get {
            val ident = call.request.headers["ident"] ?: throw IllegalArgumentException("Failed to parse header")

            val naturligIdent = aktørregisterHttpClient.gjeldendeNorskIdent(ident)

            call.respond(HttpStatusCode.OK, NaturligIdentResponse(naturligIdent))
        }
    }
}

private data class NaturligIdentResponse(val naturligIdent: String)

private data class AktørInfoRequest(
    val aktørType: AktørType,
    val id: String
)

private data class AktørInfoResponse(val name: String, val aktørType: AktørType, val id: String)

private enum class AktørType {
    AKTOER_ID,
    NATURLIG_IDENT,
    ORGANISASJON
}