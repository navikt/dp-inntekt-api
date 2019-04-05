package no.nav.dagpenger.inntekt.inntjeningsperiode

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.LocalDate

class InntjeningsperiodeTest {

    @Test
    fun ` Should compare beregningsdatoer correctly`() {
        assert(isSammeInntjeningsPeriode(
            LocalDate.of(2019, 4, 14),
            LocalDate.of(2019, 4, 15)
        ))
        assert(isSammeInntjeningsPeriode(
            LocalDate.of(2019, 3, 6),
            LocalDate.of(2019, 4, 4)
        ))

        assertFalse(isSammeInntjeningsPeriode(
            LocalDate.of(2019, 10, 17),
            LocalDate.of(2019, 11, 6)
        ))

        assertFalse(isSammeInntjeningsPeriode(
            LocalDate.of(2019, 7, 14),
            LocalDate.of(2019, 6, 6)
        ))
    }
}