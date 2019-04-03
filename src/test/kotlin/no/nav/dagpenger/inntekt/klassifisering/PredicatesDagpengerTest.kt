package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class PredicatesDagpengerTest {

    @Test
    fun `predicatesInntektklasseDagpenger returns correct predicates`() {
        val predicates = predicatesInntektklasseDagpenger()

        val dagpengerPredicates = listOf(
            ::isYtelseDagpengerVedArbeidsløshet
        )

        assert(predicates.containsAll(dagpengerPredicates))
        assertEquals(predicates.size, dagpengerPredicates.size)
    }

    @Test
    fun `isYtelseDagpengerVedArbeidsløshet predicates correctly`() {
        assert(isYtelseDagpengerVedArbeidsløshet(
            DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_VED_ARBEIDSLOESHET)))

        assertFalse(isYtelseDagpengerVedArbeidsløshet(
            DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))
        assertFalse(isYtelseDagpengerVedArbeidsløshet(
            DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.DAGPENGER_VED_ARBEIDSLOESHET)))
    }
}