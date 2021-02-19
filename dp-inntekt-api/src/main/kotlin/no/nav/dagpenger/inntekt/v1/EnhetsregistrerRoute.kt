package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.client.features.ClientRequestException
import io.ktor.features.BadRequestException
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.cacheControl
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.dagpenger.inntekt.oppslag.enhetsregister.EnhetsregisterClient

private const val CACHE_SECONDS = 86400

fun Route.enhetsregisteret(client: EnhetsregisterClient) {
    route("enhetsregisteret") {
        get("enhet/{orgnummer}") {
            withContext(Dispatchers.IO) {
                val orgnummer = call.parameters["orgnummer"] ?: throw BadRequestException("Orgnummer ikke spesifisert")
                try {
                    val result = client.hentEnhet(orgnummer)
                    call.response.cacheControl(CacheControl.MaxAge(CACHE_SECONDS))
                    call.respondText(result, ContentType.Application.Json)
                } catch (e: Exception) {
                    when (e) {
                        is ClientRequestException -> when (e.response?.status?.value) {
                            in 400..499 -> call.response.status(e.response!!.status)
                        }
                        else -> call.response.status(HttpStatusCode.BadGateway)
                    }
                }
            }
        }
    }
}
