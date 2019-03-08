package no.nav.dagpenger.inntekt

import no.nav.dagpenger.inntekt.v1.Aktoer
import no.nav.dagpenger.inntekt.v1.AktoerType
import no.nav.dagpenger.inntekt.v1.HentInntektListeResponse
import java.time.YearMonth

class DummyInntektkomponentClient : InntektskomponentClient {

    override fun getInntekt(aktørId: String, månedFom: YearMonth, månedTom: YearMonth): HentInntektListeResponse {
        return HentInntektListeResponse(emptyList(), Aktoer(AktoerType.AKTOER_ID, aktørId))
    }
}