package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class PredicatesSykepengerFangstFiskeTest {

    @Test
    fun `predicatesInntektklasseSykepengerFangstFiske returns correct predicates`() {
        val predicates = predicatesInntektklasseSykepengerFangstFiske()

        val sykepengerFangstFiskePredicates = listOf(
            ::isNæringSykepengerTilFisker,
            ::isYtelseSykepengerTilFiskerSomBareHarHyre
        )

        assert(predicates.containsAll(sykepengerFangstFiskePredicates))
        assertEquals(predicates.size, sykepengerFangstFiskePredicates.size)
    }

    @Test
    fun `isNæringSykepengerTilFisker predicates correctly`() {
        assert(isNæringSykepengerTilFisker(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_FISKER)))

        assertFalse(isNæringSykepengerTilFisker(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.DAGPENGER_TIL_FISKER)))
        assertFalse(isNæringSykepengerTilFisker(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_FISKER)))
    }

    @Test
    fun `isYtelseSykepengerTilFiskerSomBareHarHyre predicates correctly`() {
        assert(isYtelseSykepengerTilFiskerSomBareHarHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))

        assertFalse(isYtelseSykepengerTilFiskerSomBareHarHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))
        assertFalse(isYtelseSykepengerTilFiskerSomBareHarHyre(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))
    }
}