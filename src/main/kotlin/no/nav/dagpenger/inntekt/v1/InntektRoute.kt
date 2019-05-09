package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.klassifisering.Inntekt
import no.nav.dagpenger.inntekt.klassifisering.klassifiserInntekter
import java.time.LocalDate
import java.time.YearMonth

fun Route.inntekt(inntektskomponentClient: InntektskomponentClient, inntektStore: InntektStore) {
    route("inntekt") {
        post {
            val request = call.receive<InntektRequest>()

            val storedInntekt = inntektStore.getInntektId(request)?.let { inntektStore.getInntekt(it) }
                ?: inntektStore.insertInntekt(
                    request,
                    inntektskomponentClient.getInntekt(request.let(TO_INNTEKTKOMPONENT_REQUEST))
                )

            val klassifisertInntekt = storedInntekt.let {
                Inntekt(it.inntektId.id, klassifiserInntekter(it.inntekt))
            }

            call.respond(HttpStatusCode.OK, klassifisertInntekt)
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
            call.respond(HttpStatusCode.OK, storedInntekt)
        }
    }

    route("inntekt/uklassifisert") {
        post {
            val request = call.receive<InntektRequest>()
            val storedInntekt = inntektStore.getInntektId(request)?.let { inntektStore.getInntekt(it) }
                ?: throw InntektNotFoundException("Inntekt with for $request not found.")
            call.respond(HttpStatusCode.OK, storedInntekt)
        }
    }

    route("inntekt/uklassifisert/update") {
        post {
            val request = call.receive<StoredInntekt>()

            val compoundKey = inntektStore.getInntektCompoundKey(request.inntektId)

            inntektStore.insertInntekt(
                InntektRequest(compoundKey.aktørId, compoundKey.vedtakId, compoundKey.beregningsDato),
                request.inntekt)

            call.respond(HttpStatusCode.OK)
        }
    }
}

private const val MAX_INNTEKT_PERIODE = 38L

data class InntektRequest(
    val aktørId: String,
    val vedtakId: Long,
    val beregningsDato: LocalDate
)

val TO_INNTEKTKOMPONENT_REQUEST: (InntektRequest) -> InntektkomponentRequest = {
    val beregningsDatoMonth = YearMonth.of(it.beregningsDato.year, it.beregningsDato.month)
    val earliestMonth = beregningsDatoMonth.minusMonths(MAX_INNTEKT_PERIODE)
    InntektkomponentRequest(it.aktørId, earliestMonth, beregningsDatoMonth)
}