package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient

import no.nav.dagpenger.inntekt.klassifisering.Inntekt
import no.nav.dagpenger.inntekt.klassifisering.klassifiserInntekter
import java.time.LocalDate
import java.time.YearMonth

private val MAX_INNTEKT_PERIODE = 38L
private val LOGGER = KotlinLogging.logger {}

private val inntektCache = HashMap<InntektKey, Inntekt>()

fun Routing.inntekt(inntektskomponentClient: InntektskomponentClient) {
    route("v1/inntekt") {
        post {
            val requestBody = call.receive<InntektRequest>()

            val inntektKey = InntektKey(requestBody.beregningsDato, requestBody.vedtakId)

            val klassifiserteInntekter = inntektCache[inntektKey]?.let { it } ?: run {
                val newInntekt = getInntektFromInntektskomponenten(inntektskomponentClient, requestBody)
                // inntektCache[inntektKey] = newInntekt
                newInntekt
            }

            call.respond(HttpStatusCode.OK, klassifiserteInntekter)
        }
    }
}

fun getInntektFromInntektskomponenten(
    inntektskomponentClient: InntektskomponentClient,
    requestBody: InntektRequest
): Inntekt {
    LOGGER.info("Fetching new inntekt for $requestBody")
    val beregningsDatoMonth = YearMonth.of(requestBody.beregningsDato.year, requestBody.beregningsDato.month)
    val earliestMonth = beregningsDatoMonth.minusMonths(MAX_INNTEKT_PERIODE)

    val uklassifisertInntekt = inntektskomponentClient.getInntekt(
        requestBody.aktørId,
        earliestMonth,
        beregningsDatoMonth
    )

    return klassifiserInntekter(uklassifisertInntekt)
}

data class InntektRequest(
    val aktørId: String,
    val vedtakId: Long,
    val beregningsDato: LocalDate
)

data class InntektKey(
    val beregningsDato: LocalDate,
    val vedtaksId: Long
)