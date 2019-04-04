package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import java.time.LocalDate

fun Routing.beregningsdato() {
    route("v1/beregningsdato/{inntektsId}") {
        get {
            val inntektsId = call.parameters["inntektsId"] ?: throw MissingInntektsIdException("Missing inntektsId")

            val beregningsdatoResponse = BeregningsdatoResponse(LocalDate.of(2019, 7, 14))

            call.respond(HttpStatusCode.OK, beregningsdatoResponse)
        }
    }
}

data class BeregningsdatoResponse(
    val beregningsdato: LocalDate
)

class MissingInntektsIdException(override val message: String) : RuntimeException(message)