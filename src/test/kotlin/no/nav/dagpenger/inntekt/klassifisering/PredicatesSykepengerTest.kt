package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PredicatesSykepengerTest {

    @Test
    fun `predicatesInntektklasseSykepenger returns correct predicates`() {
        val predicates = predicatesInntektklasseSykepenger()

        val sykepengerPredicates = listOf(
            ::isYtelseSykepenger,
            ::isNæringSykepenger,
            ::isNæringSykepengerTilDagmamma,
            ::isNæringSykepengerTilJordOgSkogbrukere
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

    @Test
    fun `isNæringSykepenger predicates correctly`() {
        assert(isNæringSykepenger(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER)))

        assertFalse(isNæringSykepenger(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.VEDERLAG)))
        assertFalse(isNæringSykepenger(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SYKEPENGER)))
    }

    @Test
    fun `isNæringSykepengerTilDagmamma predicates correctly`() {
        assert(isNæringSykepengerTilDagmamma(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_DAGMAMMA)))

        assertFalse(isNæringSykepengerTilDagmamma(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER)))
        assertFalse(isNæringSykepengerTilDagmamma(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER_TIL_DAGMAMMA)))
    }

    @Test
    fun `isNæringSykepengerTilJordOgSkogbrukere predicates correctly`() {
        assert(isNæringSykepengerTilJordOgSkogbrukere(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_JORD_OG_SKOGBRUKERE)))

        assertFalse(isNæringSykepengerTilJordOgSkogbrukere(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_DAGMAMMA)))
        assertFalse(isNæringSykepengerTilJordOgSkogbrukere(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_JORD_OG_SKOGBRUKERE)))
    }
}