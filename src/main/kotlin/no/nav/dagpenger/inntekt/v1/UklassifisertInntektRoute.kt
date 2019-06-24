package no.nav.dagpenger.inntekt.v1

import io.ktor.application.ApplicationCall
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
import io.ktor.util.pipeline.PipelineContext
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektKlassifiseringsKoderJsonAdapter
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.mapping.GUIInntekt
import no.nav.dagpenger.inntekt.mapping.dataGrunnlagKlassifiseringToVerdikode
import no.nav.dagpenger.inntekt.mapping.mapFromGUIInntekt
import no.nav.dagpenger.inntekt.mapping.mapToGUIInntekt
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import java.time.LocalDate

fun Route.uklassifisertInntekt(inntektskomponentClient: InntektskomponentClient, inntektStore: InntektStore) {
    authenticate("jwt") {
        route("/uklassifisert/{aktørId}/{vedtakId}/{beregningsDato}") {
            get {
                parseRequest().run {
                    inntektStore.getInntektId(this)
                        ?.let {
                            inntektStore.getInntekt(it)
                        }?.let {
                            mapToGUIInntekt(it, Opptjeningsperiode(this.beregningsDato))
                        }?.let {
                            call.respond(HttpStatusCode.OK, it)
                        } ?: throw InntektNotFoundException("Inntekt with for $this not found.")
                }
            }
        }

        route("/uklassifisert/uncached/{aktørId}/{vedtakId}/{beregningsDato}") {
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
                val mappedInntekt = mapToGUIInntekt(storedInntekt, opptjeningsperiode)
                call.respond(HttpStatusCode.OK, mappedInntekt)
            }
        }

        route("/uklassifisert/update") {
            post {
                val request = call.receive<GUIInntekt>()
                val mappedInntekt = mapFromGUIInntekt(request)
                val storedInntekt = inntektStore.redigerInntekt(mappedInntekt)
                val key = inntektStore.getInntektCompoundKey(storedInntekt.inntektId)
                val opptjeningsperiode: Opptjeningsperiode = Opptjeningsperiode(key.beregningsDato)
                call.respond(HttpStatusCode.OK, mapToGUIInntekt(storedInntekt, opptjeningsperiode))
            }
        }
    }
    route("/verdikoder") {
        get {
            call.respondText(
                inntektKlassifiseringsKoderJsonAdapter.toJson(dataGrunnlagKlassifiseringToVerdikode.values),
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.parseRequest(): InntektRequest = runCatching {
    InntektRequest(
        aktørId = call.parameters["aktørId"]!!,
        vedtakId = call.parameters["vedtakId"]!!.toLong(),
        beregningsDato = LocalDate.parse(call.parameters["beregningsDato"]!!)
    )
}.getOrElse { t -> throw IllegalArgumentException("Failed to parse parameters", t) }

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