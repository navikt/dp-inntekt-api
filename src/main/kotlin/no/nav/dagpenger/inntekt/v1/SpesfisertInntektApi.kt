package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import java.time.LocalDate
import java.util.concurrent.Executors
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.db.Inntektparametre
import no.nav.dagpenger.inntekt.mapping.mapToSpesifisertInntekt
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

val api = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

fun Route.spesifisertInntekt(behandlingsInntektsGetter: BehandlingsInntektsGetter) {
    authenticate {
        route("spesifisert") {
            post {
                val request = call.receive<SpesifisertInntektRequest>()

                val storedInntekt = withContext(api) {
                    behandlingsInntektsGetter.getBehandlingsInntekt(
                        Inntektparametre(aktørId = request.aktørId, vedtakId = request.vedtakId, beregningsdato = request.beregningsDato, fødselnummer = request.fødselsnummer)
                    )
                }

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
    val vedtakId: String,
    val fødselsnummer: String? = null,
    val beregningsDato: LocalDate
)
