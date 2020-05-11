package no.nav.dagpenger.inntekt.klassifisering

import kotlin.test.assertFalse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PredicatesNæringsinntektTest {

    @Test
    fun `predicatesInntektklasseNæringsinntekt returns correct predicates`() {
        val predicates = predicatesInntektklasseNæringsinntekt()

        val næringsinntektPredicates = listOf(
            ::isLønnAnnetHyre,
            ::isLønnBonusHyre,
            ::isLønnFastTilleggHyre,
            ::isLønnFastlønnHyre,
            ::isLønnFeriepengerHyre,
            ::isLønnHelligdagstilleggHyre,
            ::isLønnOvertidsgodtgjørelseHyre,
            ::isLønnSluttvederlagHyre,
            ::isLønnTimelønnHyre,
            ::isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre,
            ::isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre,
            ::isLønnTrekkILoennForFerieHyre,
            ::isNæringLottKunTrygdeavgift,
            ::isNæringVederlag
            )

        assert(predicates.containsAll(næringsinntektPredicates))
        assertEquals(predicates.size, næringsinntektPredicates.size)
    }

    @Test
    fun `isLønnAnnetHyre predicates correctly`() {
        assert(isLønnAnnetHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.ANNET,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnAnnetHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.ANNET,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnAnnetHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnAnnetHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.ANNET)))
        assertFalse(isLønnAnnetHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET)))
    }

    @Test
    fun `isLønnBonusHyre predicates correctly`() {
        assert(isLønnBonusHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.BONUS,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnBonusHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.BONUS,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnBonusHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET)))
        assertFalse(isLønnBonusHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnBonusHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
    }

    @Test
    fun `isLønnFastTilleggHyre predicates correctly`() {
        assert(isLønnFastTilleggHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FAST_TILLEGG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFastTilleggHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FAST_TILLEGG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnFastTilleggHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnFastTilleggHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FAST_TILLEGG)))
        assertFalse(isLønnFastTilleggHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))
    }

    @Test
    fun `isLønnFastlønnHyre predicates correctly`() {
        assert(isLønnFastlønnHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FASTLOENN,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFastlønnHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FASTLOENN,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnFastlønnHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))
        assertFalse(isLønnFastlønnHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFastlønnHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
    }

    @Test
    fun `isLønnFeriepengerHyre predicates correctly`() {
        assert(isLønnFeriepengerHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FERIEPENGER,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFeriepengerHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FERIEPENGER,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnFeriepengerHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFeriepengerHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FERIEPENGER)))
        assertFalse(isLønnFeriepengerHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
    }

    @Test
    fun `isLønnHelligdagstilleggHyre predicates correctly`() {
        assert(isLønnHelligdagstilleggHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.HELLIGDAGSTILLEGG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnHelligdagstilleggHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.HELLIGDAGSTILLEGG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnHelligdagstilleggHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
        assertFalse(isLønnHelligdagstilleggHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
        assertFalse(isLønnHelligdagstilleggHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
    }

    @Test
    fun `isLønnOvertidsgodtgjørelseHyre predicates correctly`() {
        assert(isLønnOvertidsgodtgjørelseHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.OVERTIDSGODTGJOERELSE,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnOvertidsgodtgjørelseHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.OVERTIDSGODTGJOERELSE,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnOvertidsgodtgjørelseHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
        assertFalse(isLønnOvertidsgodtgjørelseHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
        assertFalse(isLønnOvertidsgodtgjørelseHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
    }

    @Test
    fun `isLønnSluttvederlagHyre predicates correctly`() {
        assert(isLønnSluttvederlagHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.SLUTTVEDERLAG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnSluttvederlagHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.SLUTTVEDERLAG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnSluttvederlagHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
        assertFalse(isLønnSluttvederlagHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SLUTTVEDERLAG)))
        assertFalse(isLønnSluttvederlagHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG)))
    }

    @Test
    fun `isLønnTimelønnHyre predicates correctly`() {
        assert(isLønnTimelønnHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TIMELOENN,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnTimelønnHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TIMELOENN,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnTimelønnHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG)))
        assertFalse(isLønnTimelønnHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.TIMELOENN)))
        assertFalse(isLønnTimelønnHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))
    }

    @Test
    fun `isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre predicates correctly`() {
        assert(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
    }

    @Test
    fun `isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre predicates correctly`() {
        assert(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))
    }

    @Test
    fun `isLønnTrekkILoennForFerieHyre predicates correctly`() {
        assert(isLønnTrekkILoennForFerieHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnTrekkILoennForFerieHyre(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnTrekkILoennForFerieHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))
        assertFalse(isLønnTrekkILoennForFerieHyre(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))
        assertFalse(isLønnTrekkILoennForFerieHyre(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))
    }

    @Test
    fun `isNæringLottKunTrygdeavgift predicates correctly`() {
        assert(isNæringLottKunTrygdeavgift(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOTT_KUN_TRYGDEAVGIFT)))

        assertFalse(isNæringLottKunTrygdeavgift(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.VEDERLAG)))
        assertFalse(isNæringLottKunTrygdeavgift(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.LOTT_KUN_TRYGDEAVGIFT)))
    }

    @Test
    fun `isNæringVederlag predicates correctly`() {
        assert(isNæringVederlag(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.VEDERLAG)))

        assertFalse(isNæringVederlag(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOTT_KUN_TRYGDEAVGIFT)))
        assertFalse(isNæringVederlag(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.VEDERLAG)))
    }
}
