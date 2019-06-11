package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektKlassifiseringsKoderJsonAdapter
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import no.nav.dagpenger.inntekt.klassifisering.Inntekt
import no.nav.dagpenger.inntekt.klassifisering.klassifiserInntekter
import no.nav.dagpenger.inntekt.mapping.GUIInntekt
import no.nav.dagpenger.inntekt.mapping.dataGrunnlagKlassifiseringToVerdikode
import no.nav.dagpenger.inntekt.mapping.mapFromGUIInntekt
import no.nav.dagpenger.inntekt.mapping.mapToGUIInntekt
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import java.time.LocalDate

fun Route.inntekt(inntektskomponentClient: InntektskomponentClient, inntektStore: InntektStore) {
    authenticate {
        route("inntekt") {
            post {
                val request = call.receive<InntektRequest>()

                val opptjeningsperiode: Opptjeningsperiode = Opptjeningsperiode(request.beregningsDato)

                val storedInntekt = inntektStore.getInntektId(request)?.let { inntektStore.getInntekt(it) }
                    ?: inntektStore.insertInntekt(
                        request,
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
    }

    route("inntekt/uklassifisert/{aktørId}/{vedtakId}/{beregningsDato}") {
        get {
            val request = try {
                InntektRequest(
                    aktørId = call.parameters["aktørId"]!!,
                    vedtakId = call.parameters["vedtakId"]!!.toLong(),
                    beregningsDato = LocalDate.parse(call.parameters["beregningsDato"]!!)
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to parse parameters", e)
            }

            val storedInntekt = inntektStore.getInntektId(request)?.let { inntektStore.getInntekt(it) }
                ?: throw InntektNotFoundException("Inntekt with for $request not found.")
            val mappedInntekt = mapToGUIInntekt(storedInntekt)
            call.respond(HttpStatusCode.OK, mappedInntekt)
        }
    }

    route("inntekt/uklassifisert/uncached/{aktørId}/{vedtakId}/{beregningsDato}") {
        get {
            val request = try {
                InntektRequest(
                    aktørId = call.parameters["aktørId"]!!,
                    vedtakId = call.parameters["vedtakId"]!!.toLong(),
                    beregningsDato = LocalDate.parse(call.parameters["beregningsDato"]!!)
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to parse parameters", e)
            }

            val opptjeningsperiode = Opptjeningsperiode(request.beregningsDato)
            val uncachedInntekt =
                inntektskomponentClient.getInntekt(toInntektskomponentRequest(request, opptjeningsperiode))
            val storedInntekt = inntektStore.insertInntekt(request, uncachedInntekt)
            val mappedInntekt = mapToGUIInntekt(storedInntekt)
            call.respond(HttpStatusCode.OK, mappedInntekt)
        }
    }

    route("inntekt/uklassifisert/update") {
        post {
            val request = call.receive<GUIInntekt>()
            val mappedInntekt = mapFromGUIInntekt(request)
            val storedInntekt = inntektStore.redigerInntekt(mappedInntekt)
            call.respond(HttpStatusCode.OK, mapToGUIInntekt(storedInntekt))
        }
    }

    route("inntekt/verdikoder") {
        get {
            call.respondText(
                inntektKlassifiseringsKoderJsonAdapter.toJson(dataGrunnlagKlassifiseringToVerdikode.values),
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        }
    }
}

data class InntektRequest(
    val aktørId: String,
    val vedtakId: Long,
    val beregningsDato: LocalDate
)

val toInntektskomponentRequest: (InntektRequest, Opptjeningsperiode) -> InntektkomponentRequest =
    { inntektRequest: InntektRequest, opptjeningsperiode: Opptjeningsperiode ->
        val sisteAvsluttendeKalendermåned = opptjeningsperiode.sisteAvsluttendeKalenderMåned
        val førsteMåned = opptjeningsperiode.førsteMåned
        InntektkomponentRequest(inntektRequest.aktørId, førsteMåned, sisteAvsluttendeKalendermåned)
    }