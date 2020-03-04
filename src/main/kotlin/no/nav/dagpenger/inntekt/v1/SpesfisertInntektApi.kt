package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.mapping.mapToSpesifisertInntekt
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import java.time.LocalDate

fun Route.spesifisertInntekt(behandlingsInntektsGetter: BehandlingsInntektsGetter) {
    authenticate {
        route("spesifisert") {
            post {
                val request = call.receive<SpesifisertInntektRequest>()

                val storedInntekt = behandlingsInntektsGetter.getBehandlingsInntekt(
                    BehandlingsKey(request.aktørId, request.vedtakId, request.beregningsDato)
                )

                val sisteAvsluttendeKalenderMåned =
                    Opptjeningsperiode(request.beregningsDato).sisteAvsluttendeKalenderMåned

                val specifiedInntekt = mapToSpesifisertInntekt(storedInntekt, sisteAvsluttendeKalenderMåned)

                call.respond(HttpStatusCode.OK, specifiedInntekt)
            }
        }
    }
}

data class SpesifisertInntektRequest(
    val aktørId: String,
    val vedtakId: Long? = null,
    val beregningsDato: LocalDate
)