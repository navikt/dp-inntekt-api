package no.nav.dagpenger.inntekt.v2

import io.ktor.routing.Routing
import io.ktor.routing.route
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.v1.inntekt

fun Routing.v2(behandlingsInntektsGetter: BehandlingsInntektsGetter) {
    route("v2/") {
        this.inntekt(behandlingsInntektsGetter)
    }
}
