package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.BadRequestException
import no.nav.dagpenger.inntekt.InntektskomponentClient
import java.time.LocalDate

fun Routing.beregningsdato(inntektskomponentClient: InntektskomponentClient) {
    route("v1/beregningsdato/{inntektsId}") {
        get {
            val inntektsId = call.parameters["inntektsId"] ?: throw BadRequestException()

            val beregningsdatoResponse = BeregningsdatoResponse(LocalDate.of(2019, 7, 14))

            call.respond(HttpStatusCode.OK, beregningsdatoResponse)
        }
    }
}

data class BeregningsdatoResponse(
    val beregningsdato: LocalDate
)

data class BeregningsdatoRequest(
    val inntektsId: String
)