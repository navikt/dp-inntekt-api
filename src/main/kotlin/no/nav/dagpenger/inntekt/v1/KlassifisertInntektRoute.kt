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
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.klassifisering.Inntekt
import no.nav.dagpenger.inntekt.klassifisering.klassifiserInntekter
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

fun Route.klassifisertInntekt(inntektskomponentClient: InntektskomponentClient, inntektStore: InntektStore) {
    authenticate {
        route("") {
            post {
                val request = call.receive<InntektRequest>()

                val opptjeningsperiode: Opptjeningsperiode = Opptjeningsperiode(request.beregningsDato)

                val behandlingsKey = BehandlingsKey(
                    request.aktørId,
                    request.vedtakId,
                    request.beregningsDato
                )

                val storedInntekt = inntektStore.getInntektId(behandlingsKey)?.let { inntektStore.getInntekt(it) }
                    ?: inntektStore.insertInntekt(
                        behandlingsKey,
                        inntektskomponentClient.getInntekt(toInntektskomponentRequest(request, opptjeningsperiode))
                    )

                val klassifisertInntekt = storedInntekt.let {
                    Inntekt(
                        it.inntektId.id,
                        klassifiserInntekter(it.inntekt),
                        it.manueltRedigert,
                        opptjeningsperiode.sisteAvsluttendeKalenderMåned
                    )
                }

                call.respond(HttpStatusCode.OK, klassifisertInntekt)
            }
        }

        route("/{inntektId}") {
            post {
                val request = call.receive<InntektRequest>()
                val inntektId = call.parameters["inntektId"]?.let { InntektId(it) } ?: throw Exception("fo")
                val opptjeningsperiode = Opptjeningsperiode(request.beregningsDato)

                val storedInntekt = inntektStore.getInntekt(inntektId)
                val klassifisertInntekt = storedInntekt.let {
                    Inntekt(
                        inntektsId = it.inntektId.id,
                        inntektsListe = klassifiserInntekter(it.inntekt),
                        manueltRedigert = it.manueltRedigert,
                        sisteAvsluttendeKalenderMåned = opptjeningsperiode.sisteAvsluttendeKalenderMåned
                    )
                }

                call.respond(HttpStatusCode.OK, klassifisertInntekt)
            }
        }
    }
}