package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType
import no.nav.dagpenger.inntekt.v1.SpesielleInntjeningsforhold

fun predicatesInntektklasseArbeid(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isLønnAksjerGrunnfondsbevisTilUnderkurs,
        ::isLønnAnnet,
        ::isLønnArbeidsoppholdKost,
        ::isLønnArbeidsoppholdLosji,
        ::isLønnBeregnetSkatt,
        ::isLønnBesøksreiserHjemmetAnnet,
        ::isLønnBesøksreiserHjemmetKilometergodtgjørelseBil,
        ::isLønnBetaltUtenlandskSkatt,
        ::isLønnBil,
        ::isLønnBolig,
        ::isLønnBonus,
        ::isLønnBonusFraForsvaret,
        ::isLønnElektroniskKommunikasjon,
        ::isLønnFastBilgodtgjørelse,
        ::isLønnFastTillegg,
        ::isLønnFastlønn,
        ::isLønnFeriepenger,
        ::isLønnFondForIdrettsutøvere,
        ::isYtelseForeldrepenger,
        ::isLønnHelligdagstillegg,
        ::isLønnHonorarAkkordProsentProvisjon,
        ::isLønnHyretillegg,
        ::isLønnInnbetalingTilUtenlandskPensjonsordning,
        ::isLønnKilometergodtgjørelseBil,
        ::isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse,
        ::isLønnKostDager,
        ::isLønnKostDøgn,
        ::isLønnKostbesparelseIHjemmet,
        ::isLønnLosji,
        ::isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon,
        ::isLønnLønnForBarnepassIBarnetsHjem,
        ::isLønnLønnTilPrivatpersonerForArbeidIHjemmet,
        ::isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon,
        ::isLønnLønnTilVergeFraFylkesmannen,
        ::isLønnOpsjoner,
        ::isLønnOvertidsgodtgjørelse,
        ::isLønnReiseAnnet,
        ::isLønnReiseKost,
        ::isLønnReiseLosji,
        ::isLønnRentefordelLån,
        ::isLønnSkattepliktigDelForsikringer,
        ::isLønnSluttvederlag,
        ::isLønnSmusstillegg,
        ::isLønnStipend,
        ::isLønnStyrehonorarOgGodtgjørelseVerv,
        ::isYtelseSvangerskapspenger,
        ::isLønnTimelønn,
        ::isLønnTrekkILønnForFerie,
        ::isLønnUregelmessigeTilleggKnyttetTilArbeidetTid,
        ::isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid,
        ::isLønnYrkebilTjenestligbehovKilometer,
        ::isLønnYrkebilTjenestligbehovListepris
    )
}

fun isLønnAksjerGrunnfondsbevisTilUnderkurs(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS

fun isLønnAnnet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.ANNET &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnArbeidsoppholdKost(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.ARBEIDSOPPHOLD_KOST

fun isLønnArbeidsoppholdLosji(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.ARBEIDSOPPHOLD_LOSJI

fun isLønnBeregnetSkatt(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BEREGNET_SKATT

fun isLønnBesøksreiserHjemmetAnnet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BESOEKSREISER_HJEMMET_ANNET

fun isLønnBesøksreiserHjemmetKilometergodtgjørelseBil(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BESOEKSREISER_HJEMMET_KILOMETERGODTGJOERELSE_BIL

fun isLønnBetaltUtenlandskSkatt(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BETALT_UTENLANDSK_SKATT

fun isLønnBil(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BIL

fun isLønnBolig(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BOLIG

fun isLønnBonus(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BONUS &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnBonusFraForsvaret(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.BONUS_FRA_FORSVARET

fun isLønnElektroniskKommunikasjon(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.ELEKTRONISK_KOMMUNIKASJON

fun isLønnFastBilgodtgjørelse(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FAST_BILGODTGJOERELSE

fun isLønnFastTillegg(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FAST_TILLEGG &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnFastlønn(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FASTLOENN &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnFeriepenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FERIEPENGER &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnFondForIdrettsutøvere(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FOND_FOR_IDRETTSUTOEVERE

fun isYtelseForeldrepenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.FORELDREPENGER

fun isLønnHelligdagstillegg(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.HELLIGDAGSTILLEGG &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnHonorarAkkordProsentProvisjon(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.HONORAR_AKKORD_PROSENT_PROVISJON

fun isLønnHyretillegg(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.HYRETILLEGG

fun isLønnInnbetalingTilUtenlandskPensjonsordning(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING

fun isLønnKilometergodtgjørelseBil(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.KILOMETERGODTGJOERELSE_BIL

fun isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.KOMMUNAL_OMSORGSLOENN_OG_FOSTERHJEMSGODTGJOERELSE

fun isLønnKostDager(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.KOST_DAGER

fun isLønnKostDøgn(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.KOST_DOEGN

fun isLønnKostbesparelseIHjemmet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.KOSTBESPARELSE_I_HJEMMET

fun isLønnLosji(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.LOSJI

fun isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.IKKE_SKATTEPLIKTIG_LOENN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON

fun isLønnLønnForBarnepassIBarnetsHjem(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.LOENN_FOR_BARNEPASS_I_BARNETS_HJEM

fun isLønnLønnTilPrivatpersonerForArbeidIHjemmet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.LOENN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET

fun isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.LOENN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON

fun isLønnLønnTilVergeFraFylkesmannen(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.LOENN_TIL_VERGE_FRA_FYLKESMANNEN

fun isLønnOpsjoner(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.OPSJONER

fun isLønnOvertidsgodtgjørelse(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.OVERTIDSGODTGJOERELSE &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnReiseAnnet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.REISE_ANNET

fun isLønnReiseKost(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.REISE_KOST

fun isLønnReiseLosji(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.REISE_LOSJI

fun isLønnRentefordelLån(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.RENTEFORDEL_LAAN

fun isLønnSkattepliktigDelForsikringer(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SKATTEPLIKTIG_DEL_FORSIKRINGER

fun isLønnSluttvederlag(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SLUTTVEDERLAG &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnSmusstillegg(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SMUSSTILLEGG

fun isLønnStipend(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.STIPEND

fun isLønnStyrehonorarOgGodtgjørelseVerv(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.STYREHONORAR_OG_GODTGJOERELSE_VERV

fun isYtelseSvangerskapspenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.SVANGERSKAPSPENGER

fun isLønnTimelønn(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.TIMELOENN &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnTrekkILønnForFerie(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY &&
        datagrunnlag.forhold != SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK

fun isLønnYrkebilTjenestligbehovKilometer(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_KILOMETER

fun isLønnYrkebilTjenestligbehovListepris(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS