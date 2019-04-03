package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class PredicatesDagpengerFangstFiskeTest {

    @Test
    fun `predicatesInntektklasseDagpengerFangstFiske returns correct predicates`() {
        val predicates = predicatesInntektklasseDagpengerFangstFiske()

        val dagpengerFangstFiskePredicates = listOf(
            ::isNæringDagpengerTilFisker,
            ::isYtelseDagpengerTilFiskerSomBareHarHyre
        )

        assert(predicates.containsAll(dagpengerFangstFiskePredicates))
        assertEquals(predicates.size, dagpengerFangstFiskePredicates.size)
    }

    @Test
    fun `isNæringDagpengerTilFisker predicates correctly`() {
        assert(isNæringDagpengerTilFisker(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.DAGPENGER_TIL_FISKER)))

        assertFalse(isNæringDagpengerTilFisker(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.ANNET)))
        assertFalse(isNæringDagpengerTilFisker(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_TIL_FISKER)))
    }

    @Test
    fun `isYtelseDagpengerTilFiskerSomBareHarHyre predicates correctly`() {
        assert(isYtelseDagpengerTilFiskerSomBareHarHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))

        assertFalse(isYtelseDagpengerTilFiskerSomBareHarHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))
        assertFalse(isYtelseDagpengerTilFiskerSomBareHarHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE)))
    }
}