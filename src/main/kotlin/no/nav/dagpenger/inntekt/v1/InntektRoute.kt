package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.klassifisering.Inntekt
import no.nav.dagpenger.inntekt.klassifisering.klassifiserInntekter
import java.time.LocalDate
import java.time.YearMonth

fun Routing.inntekt(inntektskomponentClient: InntektskomponentClient, inntektStore: InntektStore) {
    route("v1/inntekt") {
        post {
            val request = call.receive<InntektRequest>()

            val storedInntekt = inntektStore.getInntektId(request)?.let { inntektStore.getInntekt(it) }
                    ?: inntektStore.insertInntekt(request, inntektskomponentClient.getInntekt(request.let(TO_INNTEKTKOMPONENT_REQUEST)))

            val klassifisertInntekt = storedInntekt.let {
                Inntekt(it.inntektId.id, klassifiserInntekter(it.inntekt))
            }

            call.respond(HttpStatusCode.OK, klassifisertInntekt)
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