package no.nav.dagpenger.inntekt.v1

import com.auth0.jwt.exceptions.JWTDecodeException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
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
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.ManueltRedigert
import no.nav.dagpenger.inntekt.inntektKlassifiseringsKoderJsonAdapter
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.mapping.GUIInntekt
import no.nav.dagpenger.inntekt.mapping.Inntektsmottaker
import no.nav.dagpenger.inntekt.mapping.dataGrunnlagKlassifiseringToVerdikode
import no.nav.dagpenger.inntekt.mapping.mapToDetachedInntekt
import no.nav.dagpenger.inntekt.mapping.mapToGUIInntekt
import no.nav.dagpenger.inntekt.mapping.mapToStoredInntekt
import no.nav.dagpenger.inntekt.oppslag.OppslagClient
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import java.time.LocalDate

private val LOGGER = KotlinLogging.logger {}

fun Route.uklassifisertInntekt(
    inntektskomponentClient: InntektskomponentClient,
    inntektStore: InntektStore,
    oppslagClient: OppslagClient
) {
    authenticate("jwt") {
        route("/uklassifisert/{aktørId}/{vedtakId}/{beregningsDato}") {
            get {
                parseUrlPathParameters().run {
                    inntektStore.getInntektId(BehandlingsKey(this.aktørId, this.vedtakId, this.beregningsDato))
                        ?.let {
                            inntektStore.getInntekt(it)
                        }?.let {
                            val personNummer = oppslagClient.finnNaturligIdent(this.aktørId)
                            val navn = personNummer?.let { pnr -> oppslagClient.personNavn(pnr) }
                            val inntektsmottaker = Inntektsmottaker(personNummer, navn)
                            mapToGUIInntekt(it, Opptjeningsperiode(this.beregningsDato), inntektsmottaker)
                        }?.let {
                            call.respond(HttpStatusCode.OK, it)
                        } ?: throw InntektNotFoundException("Inntekt with for $this not found.")
                }
            }
            post {
                parseUrlPathParameters().run {
                    val guiInntekt = call.receive<GUIInntekt>()
                    mapToStoredInntekt(guiInntekt)
                        .let {
                            inntektStore.insertInntekt(
                                BehandlingsKey(this.aktørId, this.vedtakId, this.beregningsDato),
                                it.inntekt,
                                ManueltRedigert.from(guiInntekt.redigertAvSaksbehandler, getSubject()))
                        }
                        .let {
                            call.respond(
                                HttpStatusCode.OK,
                                mapToGUIInntekt(it, Opptjeningsperiode(this.beregningsDato), guiInntekt.inntektsmottaker)
                            )
                        }
                }
            }
        }

        route("/uklassifisert/uncached/{aktørId}/{vedtakId}/{beregningsDato}") {
            get {
                parseUrlPathParameters().run {
                    val opptjeningsperiode = Opptjeningsperiode(this.beregningsDato)
                    toInntektskomponentRequest(this, opptjeningsperiode)
                        .let {
                            inntektskomponentClient.getInntekt(it)
                        }
                        .let {
                            val personNummer = oppslagClient.finnNaturligIdent(this.aktørId)
                            val navn = personNummer?.let { pnr -> oppslagClient.personNavn(pnr) }
                            val inntektsmottaker = Inntektsmottaker(personNummer, navn)
                            mapToGUIInntekt(it, opptjeningsperiode, inntektsmottaker)
                        }
                        .let {
                            call.respond(HttpStatusCode.OK, it)
                        }
                }
            }

            post {
                parseUrlPathParameters().run {
                    val guiInntekt = call.receive<GUIInntekt>()
                    mapToDetachedInntekt(guiInntekt)
                        .let {
                            inntektStore.insertInntekt(
                                BehandlingsKey(this.aktørId, this.vedtakId, this.beregningsDato),
                                it.inntekt,
                                ManueltRedigert.from(guiInntekt.redigertAvSaksbehandler, getSubject()))
                        }
                        .let {
                            call.respond(
                                HttpStatusCode.OK,
                                mapToGUIInntekt(it, Opptjeningsperiode(this.beregningsDato), guiInntekt.inntektsmottaker)
                            )
                        }
                }
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

private fun PipelineContext<Unit, ApplicationCall>.getSubject(): String {
    return runCatching {
        call.authentication.principal?.let {
            (it as JWTPrincipal).payload.subject
        } ?: throw JWTDecodeException("Unable to get subject from JWT")
    }.getOrElse {
        LOGGER.error(it) { "Unable to get subject" }
        return@getOrElse "UNKNOWN"
    }
}

private fun PipelineContext<Unit, ApplicationCall>.parseUrlPathParameters(): InntektRequest = runCatching {
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