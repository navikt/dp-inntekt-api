package no.nav.dagpenger.inntekt.mapping

import com.uchuhimo.collections.biMapOf
import no.nav.dagpenger.events.inntekt.v1.PosteringsType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold

data class PosteringsTypeInfo(
    val type: InntektType,
    val beskrivelse: InntektBeskrivelse,
    val forhold: SpesielleInntjeningsforhold? = null
)

fun toPosteringsType(posteringsTypeInfo: PosteringsTypeInfo): PosteringsType {
    return posteringsTypeMapping[posteringsTypeInfo]
        ?: throw PosteringsTypeMappingException("No posteringsType found for posteringsTypeInfo=$posteringsTypeInfo")
}

fun toPosteringsTypeInfo(posteringsType: PosteringsType): PosteringsTypeInfo {
    return posteringsTypeMapping.inverse[posteringsType]
        ?: throw PosteringsTypeMappingException("No posteringsTypeInfo found for posteringsType=$posteringsType")
}

private val posteringsTypeMapping = biMapOf(
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS, null) to PosteringsType.L_AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET, null) to PosteringsType.L_ANNET,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_KOST, null) to PosteringsType.L_ARBEIDSOPPHOLD_KOST,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_LOSJI, null) to PosteringsType.L_ARBEIDSOPPHOLD_LOSJI,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BEREGNET_SKATT, null) to PosteringsType.L_BEREGNET_SKATT,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_ANNET, null) to PosteringsType.L_BESØKSREISER_HJEMMET_ANNET,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_KILOMETERGODTGJOERELSE_BIL, null) to PosteringsType.L_BESØKSREISER_HJEMMET_KILOMETERGODTGJØRELSE_BIL,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BETALT_UTENLANDSK_SKATT, null) to PosteringsType.L_BETALT_UTENLANDSK_SKATT,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BIL, null) to PosteringsType.L_BIL,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BOLIG, null) to PosteringsType.L_BOLIG,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS, null) to PosteringsType.L_BONUS,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS_FRA_FORSVARET, null) to PosteringsType.L_BONUS_FRA_FORSVARET,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ELEKTRONISK_KOMMUNIKASJON, null) to PosteringsType.L_ELEKTRONISK_KOMMUNIKASJON,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_BILGODTGJOERELSE, null) to PosteringsType.L_FAST_BILGODTGJØRELSE,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG, null) to PosteringsType.L_FAST_TILLEGG,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN, null) to PosteringsType.L_FASTLØNN,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER, null) to PosteringsType.L_FERIEPENGER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FOND_FOR_IDRETTSUTOEVERE, null) to PosteringsType.L_FOND_FOR_IDRETTSUTØVERE,
    PosteringsTypeInfo(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FORELDREPENGER, null) to PosteringsType.Y_FORELDREPENGER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG, null) to PosteringsType.L_HELLIGDAGSTILLEGG,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HONORAR_AKKORD_PROSENT_PROVISJON, null) to PosteringsType.L_HONORAR_AKKORD_PROSENT_PROVISJON,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HYRETILLEGG, null) to PosteringsType.L_HYRETILLEGG,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING, null) to PosteringsType.L_INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KILOMETERGODTGJOERELSE_BIL, null) to PosteringsType.L_KILOMETERGODTGJØRELSE_BIL,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOMMUNAL_OMSORGSLOENN_OG_FOSTERHJEMSGODTGJOERELSE, null) to PosteringsType.L_KOMMUNAL_OMSORGSLØNN_OG_FOSTERHJEMSGODTGJØRELSE,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DAGER, null) to PosteringsType.L_KOST_DAGER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DOEGN, null) to PosteringsType.L_KOST_DØGN,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOSTBESPARELSE_I_HJEMMET, null) to PosteringsType.L_KOSTBESPARELSE_I_HJEMMET,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOSJI, null) to PosteringsType.L_LOSJI,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.IKKE_SKATTEPLIKTIG_LOENN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON, null) to PosteringsType.L_IKKE_SKATTEPLIKTIG_LØNN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_FOR_BARNEPASS_I_BARNETS_HJEM, null) to PosteringsType.L_LØNN_FOR_BARNEPASS_I_BARNETS_HJEM,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET, null) to PosteringsType.L_LØNN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON, null) to PosteringsType.L_LØNN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_TIL_VERGE_FRA_FYLKESMANNEN, null) to PosteringsType.L_LØNN_TIL_VERGE_FRA_FYLKESMANNEN,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OPSJONER, null) to PosteringsType.L_OPSJONER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE, null) to PosteringsType.L_OVERTIDSGODTGJØRELSE,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_ANNET, null) to PosteringsType.L_REISE_ANNET,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_KOST, null) to PosteringsType.L_REISE_KOST,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_LOSJI, null) to PosteringsType.L_REISE_LOSJI,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.RENTEFORDEL_LAAN, null) to PosteringsType.L_RENTEFORDEL_LÅN,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SKATTEPLIKTIG_DEL_FORSIKRINGER, null) to PosteringsType.L_SKATTEPLIKTIG_DEL_FORSIKRINGER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG, null) to PosteringsType.L_SLUTTVEDERLAG,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SMUSSTILLEGG, null) to PosteringsType.L_SMUSSTILLEGG,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.STIPEND, null) to PosteringsType.L_STIPEND,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.STYREHONORAR_OG_GODTGJOERELSE_VERV, null) to PosteringsType.L_STYREHONORAR_OG_GODTGJØRELSE_VERV,
    PosteringsTypeInfo(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SVANGERSKAPSPENGER, null) to PosteringsType.Y_SVANGERSKAPSPENGER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN, null) to PosteringsType.L_TIMELØNN,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE, null) to PosteringsType.L_TREKK_I_LØNN_FOR_FERIE,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID, null) to PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID, null) to PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_KILOMETER, null) to PosteringsType.L_YRKEBIL_TJENESTLIGBEHOV_KILOMETER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS, null) to PosteringsType.L_YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS,
    PosteringsTypeInfo(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_VED_ARBEIDSLOESHET, null) to PosteringsType.Y_DAGPENGER_VED_ARBEIDSLØSHET,
    PosteringsTypeInfo(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.DAGPENGER_TIL_FISKER, null) to PosteringsType.N_DAGPENGER_TIL_FISKER,
    PosteringsTypeInfo(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE, null) to PosteringsType.Y_DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE,
    PosteringsTypeInfo(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SYKEPENGER_TIL_FISKER, null) to PosteringsType.N_SYKEPENGER_TIL_FISKER,
    PosteringsTypeInfo(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE, null) to PosteringsType.Y_SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_ANNET_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_BONUS_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_FAST_TILLEGG_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_FASTLØNN_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_FERIEPENGER_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_HELLIGDAGSTILLEGG_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_OVERTIDSGODTGJØRELSE_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_SLUTTVEDERLAG_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_TIMELØNN_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID_H,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID_H,
    PosteringsTypeInfo(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOTT_KUN_TRYGDEAVGIFT, null) to PosteringsType.N_LOTT_KUN_TRYGDEAVGIFT,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE, SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY) to PosteringsType.L_TREKK_I_LØNN_FOR_FERIE_H,
    PosteringsTypeInfo(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.VEDERLAG, null) to PosteringsType.N_VEDERLAG,
    PosteringsTypeInfo(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SYKEPENGER, null) to PosteringsType.Y_SYKEPENGER,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_ANNET_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_BONUS_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_FAST_TILLEGG_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_FASTLØNN_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_FERIEPENGER_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_HELLIGDAGSTILLEGG_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_OVERTIDSGODTGJØRELSE_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_SLUTTVEDERLAG_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_TIMELØNN_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE, SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK) to PosteringsType.L_TREKK_I_LØNN_FOR_FERIE_T,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIPS, null) to PosteringsType.L_TIPS,
    PosteringsTypeInfo(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SKATTEPLIKTIG_PERSONALRABATT, null) to PosteringsType.L_SKATTEPLIKTIG_PERSONALRABATT

)

class PosteringsTypeMappingException(message: String) : RuntimeException(message)