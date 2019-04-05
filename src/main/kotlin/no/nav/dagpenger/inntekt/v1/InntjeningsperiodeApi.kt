package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.inntjeningsperiode.isSammeInntjeningsPeriode
import no.nav.dagpenger.inntekt.v1.models.InntjeningsperiodeParametre
import no.nav.dagpenger.inntekt.v1.models.InntjeningsperiodeResultat
import java.time.LocalDate

fun Route.inntjeningsperiodeApi() {

    route("/v1/inntjeningsperiode") {
        post {
            val parametere = call.receive<InntjeningsperiodeParametre>()

            val gammelBeregningsdato = LocalDate.of(2019, 2, 27)

            val resultat = isSammeInntjeningsPeriode(gammelBeregningsdato, LocalDate.parse(parametere.beregningsdato))

            val response = InntjeningsperiodeResultat(resultat, parametere)

            call.respond(HttpStatusCode.OK, response)
        }
    }
}