package no.nav.dagpenger.inntekt

import no.nav.dagpenger.inntekt.v1.HentInntektListeResponse
import java.time.YearMonth

interface InntektskomponentClient {

    fun getInntekt(fnr: String, månedFom: YearMonth, månedTom: YearMonth): HentInntektListeResponse
}
