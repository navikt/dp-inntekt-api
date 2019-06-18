package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PredicatesSykepengerTest {

    @Test
    fun `predicatesInntektklasseSykepenger returns correct predicates`() {
        val predicates = predicatesInntektklasseSykepenger()

        val sykepengerPredicates = listOf(
            ::isYtelseSykepenger
        )

        assert(predicates.containsAll(sykepengerPredicates))
        assertEquals(predicates.size, sykepengerPredicates.size)
    }

    @Test
    fun `isYtelseSykepenger predicates correctly`() {
        assert(isYtelseSykepenger(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER)))

        assertFalse(isYtelseSykepenger(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.VEDERLAG)))
        assertFalse(isYtelseSykepenger(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER)))
    }
}