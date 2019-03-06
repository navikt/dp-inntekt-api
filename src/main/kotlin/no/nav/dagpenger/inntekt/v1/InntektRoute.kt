package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.InntektskomponentClient
import no.nav.dagpenger.inntekt.klassifisering.klassifiserInntekter
import java.time.LocalDate
import java.time.YearMonth

val MAX_INNTEKT_PERIODE = 38L

fun Routing.inntekt(inntektskomponentClient: InntektskomponentClient) {
    route("v1/inntekt") {
        post {
            val requestBody = call.receive<InntektRequest>()

            val beregningsDatoMonth = YearMonth.of(requestBody.beregningsDato.year, requestBody.beregningsDato.month)
            val earliestMonth = beregningsDatoMonth.minusMonths(MAX_INNTEKT_PERIODE)

            val uklassifisertInntekt = inntektskomponentClient.getInntekt(
                requestBody.aktørId,
                earliestMonth,
                beregningsDatoMonth
            )

            val klassifiserteInntekter = klassifiserInntekter(uklassifisertInntekt)

            call.respond(HttpStatusCode.OK, klassifiserteInntekter)
        }
    }
}

data class InntektRequest(
    val aktørId: String,
    val vedtakId: String,
    val beregningsDato: LocalDate
)