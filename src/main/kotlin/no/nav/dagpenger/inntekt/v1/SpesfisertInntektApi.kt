package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.mapping.mapToSpesifisertInntekt
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import java.time.LocalDate

fun Route.spesifisertInntekt(behandlingsInntektsGetter: BehandlingsInntektsGetter, inntektStore: InntektStore) {
    authenticate {
        route("spesifisert") {
            post {
                val request = call.receive<SpesifisertInntektRequest>()

                val storedInntekt = behandlingsInntektsGetter.getBehandlingsInntekt(
                    BehandlingsKey(request.aktørId, request.vedtakId, request.beregningsDato)
                )

                val sisteAvsluttendeKalenderMåned = Opptjeningsperiode(request.beregningsDato).sisteAvsluttendeKalenderMåned
                val spesifisertInntekt = mapToSpesifisertInntekt(storedInntekt, sisteAvsluttendeKalenderMåned)

                call.respond(HttpStatusCode.OK, spesifisertInntekt)
            }
        }
    }

    route("spesifisert/{inntektId}") {
        post {
            val request = call.receive<InntektByIdRequest>()
            val inntektId = call.parameters["inntektId"].runCatching {
                InntektId(call.parameters["inntektId"]!!)
            }.getOrThrow()

            val aktor = Aktoer(
                aktoerType = AktoerType.AKTOER_ID,
                identifikator = request.aktørId
            )

            val storedInntekt = inntektStore.getInntekt(inntektId)

            if (storedInntekt.inntekt.ident != aktor) {
                throw InntektNotAuthorizedException("Aktøren har ikke tilgang til denne inntekten.")
            }

            val sisteAvsluttendeKalenderMåned = Opptjeningsperiode(request.beregningsDato).sisteAvsluttendeKalenderMåned
            val spesifisertInntekt = mapToSpesifisertInntekt(storedInntekt, sisteAvsluttendeKalenderMåned)

            call.respond(HttpStatusCode.OK, spesifisertInntekt)
        }
    }
}

data class InntektByIdRequest(
    val aktørId: String,
    val beregningsDato: LocalDate
)

data class SpesifisertInntektRequest(
    val aktørId: String,
    val vedtakId: Long,
    val beregningsDato: LocalDate
)