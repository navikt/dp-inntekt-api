package no.nav.dagpenger.inntekt.v1

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import no.nav.dagpenger.inntekt.v1.models.InntjeningsperiodeParametre
import no.nav.dagpenger.inntekt.v1.models.InntjeningsperiodeResultat
import java.time.LocalDate

fun Route.opptjeningsperiodeApi(inntektStore: InntektStore) {

    route("is-samme-inntjeningsperiode") {
        post {
            withContext(Dispatchers.IO) {
                val parametere = call.receive<InntjeningsperiodeParametre>()

                val gammelBeregningsdato = inntektStore.getBeregningsdato(InntektId(parametere.inntektsId))

                val resultat = Opptjeningsperiode(gammelBeregningsdato).sammeOpptjeningsPeriode(
                    Opptjeningsperiode(
                        LocalDate.parse(parametere.beregningsdato)
                    )
                )

                val response = InntjeningsperiodeResultat(resultat, parametere)

                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}
