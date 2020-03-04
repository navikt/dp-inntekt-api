package no.nav.dagpenger.inntekt.v1.models

import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import java.time.LocalDateTime

data class Inntekt(
    val inntektId: InntektId,
    val inntekt: InntektkomponentResponse,
    val manueltRedigert: Boolean,
    val timestamp: LocalDateTime? = null
)
