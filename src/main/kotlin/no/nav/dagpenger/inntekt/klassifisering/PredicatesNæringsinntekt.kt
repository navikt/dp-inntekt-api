package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold

fun predicatesInntektklasseNæringsinntekt(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isNæringAnnet,
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
}

fun isNæringAnnet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.ANNET

fun isLønnAnnetHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.ANNET &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnBonusHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.BONUS &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnFastTilleggHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.FAST_TILLEGG &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnFastlønnHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.FASTLOENN &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnFeriepengerHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.FERIEPENGER &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnHelligdagstilleggHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.HELLIGDAGSTILLEGG &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnOvertidsgodtgjørelseHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.OVERTIDSGODTGJOERELSE &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnSluttvederlagHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.SLUTTVEDERLAG &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnTimelønnHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.TIMELOENN &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isLønnTrekkILoennForFerieHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT &&
        datagrunnlag.beskrivelse == InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE &&
        datagrunnlag.forhold == SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY

fun isNæringLottKunTrygdeavgift(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.LOTT_KUN_TRYGDEAVGIFT

fun isNæringVederlag(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.VEDERLAG