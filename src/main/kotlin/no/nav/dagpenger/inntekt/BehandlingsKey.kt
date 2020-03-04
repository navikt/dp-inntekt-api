package no.nav.dagpenger.inntekt

import java.time.LocalDate

data class BehandlingsKey(
    val aktørId: String,
    val vedtakId: Long? = null,
    val beregningsDato: LocalDate
)
