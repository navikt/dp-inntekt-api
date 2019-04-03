package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold

fun predicatesInntektklasseTiltakslønn(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
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
}
fun isLønnAnnetTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.ANNET &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnBonusTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.BONUS &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnFastTilleggTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.FAST_TILLEGG &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnFastlønnTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.FASTLOENN &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnFeriepengerTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.FERIEPENGER &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnHelligdagstilleggTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.HELLIGDAGSTILLEGG &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnOvertidsgodtgjørelseTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.OVERTIDSGODTGJOERELSE &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnSluttvederlagTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.SLUTTVEDERLAG &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnTimelønnTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.TIMELOENN &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnTrekkILoennForFerieTiltak(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK