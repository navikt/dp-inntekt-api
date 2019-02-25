package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.v1.klassifisering.klassifiserInntekter
import java.time.YearMonth

fun Routing.inntekt(inntektskomponentHttpClient: InntektskomponentHttpClient) {
    route("inntekt") {
        get("/{id}") {
            val aktorId = call.parameters["id"]!!

            val uklassifisertInntekt = inntektskomponentHttpClient.getInntekt(
                aktorId,
                YearMonth.of(2017, 1), YearMonth.of(2019, 1)
            )

            val klassifiserteInntekter = klassifiserInntekter(uklassifisertInntekt)

            call.respond(HttpStatusCode.OK, klassifiserteInntekter)
        }
    }
}
