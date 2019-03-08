package no.nav.dagpenger.inntekt.inntektskomponenten.v1

import java.time.YearMonth

interface InntektskomponentClient {

    fun getInntekt(aktørId: String, månedFom: YearMonth, månedTom: YearMonth): InntektkomponentenResponse
}
