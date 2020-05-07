package no.nav.dagpenger.inntekt.klassifisering

import kotlin.test.assertFalse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PredicatesTiltakslønnTest {

    @Test
    fun `predicatesInntektklasseNæringsinntekt returns correct predicates`() {
        val predicates = predicatesInntektklasseTiltakslønn()

        val næringsinntektPredicates = listOf(
            ::isLønnAnnetTiltak,
            ::isLønnBonusTiltak,
            ::isLønnFastTilleggTiltak,
            ::isLønnFastlønnTiltak,
            ::isLønnFeriepengerTiltak,
            ::isLønnHelligdagstilleggTiltak,
            ::isLønnOvertidsgodtgjørelseTiltak,
            ::isLønnSluttvederlagTiltak,
            ::isLønnTimelønnTiltak,
            ::isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak,
            ::isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak,
            ::isLønnTrekkILoennForFerieTiltak
        )

        assert(predicates.containsAll(næringsinntektPredicates))
        assertEquals(predicates.size, næringsinntektPredicates.size)
    }

    @Test
    fun `isLønnAnnetTiltak predicates correctly`() {
        assert(isLønnAnnetTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.ANNET,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnAnnetTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.ANNET,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnAnnetTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnAnnetTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.ANNET)))
        assertFalse(isLønnAnnetTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET)))
    }

    @Test
    fun `isLønnBonusTiltak predicates correctly`() {
        assert(isLønnBonusTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.BONUS,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnBonusTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.BONUS,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnBonusTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET)))
        assertFalse(isLønnBonusTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnBonusTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
    }

    @Test
    fun `isLønnFastTilleggTiltak predicates correctly`() {
        assert(isLønnFastTilleggTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FAST_TILLEGG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnFastTilleggTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FAST_TILLEGG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFastTilleggTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnFastTilleggTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FAST_TILLEGG)))
        assertFalse(isLønnFastTilleggTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))
    }

    @Test
    fun `isLønnFastlønnTiltak predicates correctly`() {
        assert(isLønnFastlønnTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FASTLOENN,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnFastlønnTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FASTLOENN,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFastlønnTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))
        assertFalse(isLønnFastlønnTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFastlønnTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
    }

    @Test
    fun `isLønnFeriepengerTiltak predicates correctly`() {
        assert(isLønnFeriepengerTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FERIEPENGER,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnFeriepengerTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FERIEPENGER,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFeriepengerTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFeriepengerTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FERIEPENGER)))
        assertFalse(isLønnFeriepengerTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
    }

    @Test
    fun `isLønnHelligdagstilleggTiltak predicates correctly`() {
        assert(isLønnHelligdagstilleggTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.HELLIGDAGSTILLEGG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnHelligdagstilleggTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.HELLIGDAGSTILLEGG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnHelligdagstilleggTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
        assertFalse(isLønnHelligdagstilleggTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
        assertFalse(isLønnHelligdagstilleggTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
    }

    @Test
    fun `isLønnOvertidsgodtgjørelseTiltak predicates correctly`() {
        assert(isLønnOvertidsgodtgjørelseTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.OVERTIDSGODTGJOERELSE,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnOvertidsgodtgjørelseTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.OVERTIDSGODTGJOERELSE,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnOvertidsgodtgjørelseTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
        assertFalse(isLønnOvertidsgodtgjørelseTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
        assertFalse(isLønnOvertidsgodtgjørelseTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
    }

    @Test
    fun `isLønnSluttvederlagTiltak predicates correctly`() {
        assert(isLønnSluttvederlagTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.SLUTTVEDERLAG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnSluttvederlagTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.SLUTTVEDERLAG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnSluttvederlagTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
        assertFalse(isLønnSluttvederlagTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SLUTTVEDERLAG)))
        assertFalse(isLønnSluttvederlagTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG)))
    }

    @Test
    fun `isLønnTimelønnTiltak predicates correctly`() {
        assert(isLønnTimelønnTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TIMELOENN,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnTimelønnTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TIMELOENN,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnTimelønnTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG)))
        assertFalse(isLønnTimelønnTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.TIMELOENN)))
        assertFalse(isLønnTimelønnTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))
    }

    @Test
    fun `isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak predicates correctly`() {
        assert(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
    }

    @Test
    fun `isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak predicates correctly`() {
        assert(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))
    }

    @Test
    fun `isLønnTrekkILoennForFerieTiltak predicates correctly`() {
        assert(isLønnTrekkILoennForFerieTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))

        assertFalse(isLønnTrekkILoennForFerieTiltak(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnTrekkILoennForFerieTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnTrekkILoennForFerieTiltak(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))
        assertFalse(isLønnTrekkILoennForFerieTiltak(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))
    }
}
