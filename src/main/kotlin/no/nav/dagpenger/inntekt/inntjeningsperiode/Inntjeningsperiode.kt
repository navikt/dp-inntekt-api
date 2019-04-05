package no.nav.dagpenger.inntekt.inntjeningsperiode

import java.time.LocalDate

internal fun isSammeInntjeningsPeriode(gammelBeregningsdato: LocalDate, nyBeregningsdato: LocalDate): Boolean {
    return senesteInntektsmåned(gammelBeregningsdato).equals(senesteInntektsmåned(nyBeregningsdato))
}