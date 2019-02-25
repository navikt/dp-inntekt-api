package no.nav.dagpenger.inntekt.v1.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

internal class PredicatesTest {

    @Test
    fun `inntektklasseArbeidPredicates returns correct predicates`() {
        val predicates = inntektklasseArbeidPredicates()

        val arbeidPredicates = listOf(
            ::isLønnFastlønn,
            ::isLønnFeriepenger
        )

        assert(predicates.containsAll(arbeidPredicates))
    }

    @Test
    fun `isLønnFastlønn predicates correctly`() {
        assert(isLønnFastlønn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))

        assertFalse(isLønnFastlønn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
        assertFalse(isLønnFastlønn(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.FASTLOENN)))
    }

    @Test
    fun `isLønnFeriepenger predicates correctly`() {
        assert(isLønnFeriepenger(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))

        assertFalse(isLønnFeriepenger(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFeriepenger(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
    }
}