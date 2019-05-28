package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import no.nav.dagpenger.inntekt.klassifisering.DatagrunnlagKlassifisering
import no.nav.dagpenger.inntekt.klassifisering.isLønnAksjerGrunnfondsbevisTilUnderkurs
import no.nav.dagpenger.inntekt.klassifisering.isLønnAnnet
import no.nav.dagpenger.inntekt.klassifisering.isLønnAnnetHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnAnnetTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnArbeidsoppholdKost
import no.nav.dagpenger.inntekt.klassifisering.isLønnArbeidsoppholdLosji
import no.nav.dagpenger.inntekt.klassifisering.isLønnBeregnetSkatt
import no.nav.dagpenger.inntekt.klassifisering.isLønnBesøksreiserHjemmetAnnet
import no.nav.dagpenger.inntekt.klassifisering.isLønnBesøksreiserHjemmetKilometergodtgjørelseBil
import no.nav.dagpenger.inntekt.klassifisering.isLønnBetaltUtenlandskSkatt
import no.nav.dagpenger.inntekt.klassifisering.isLønnBil
import no.nav.dagpenger.inntekt.klassifisering.isLønnBolig
import no.nav.dagpenger.inntekt.klassifisering.isLønnBonus
import no.nav.dagpenger.inntekt.klassifisering.isLønnBonusFraForsvaret
import no.nav.dagpenger.inntekt.klassifisering.isLønnBonusHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnBonusTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnElektroniskKommunikasjon
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastBilgodtgjørelse
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastTillegg
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastTilleggHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastTilleggTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastlønn
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastlønnHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnFastlønnTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnFeriepenger
import no.nav.dagpenger.inntekt.klassifisering.isLønnFeriepengerHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnFeriepengerTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnFondForIdrettsutøvere
import no.nav.dagpenger.inntekt.klassifisering.isLønnHelligdagstillegg
import no.nav.dagpenger.inntekt.klassifisering.isLønnHelligdagstilleggHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnHelligdagstilleggTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnHonorarAkkordProsentProvisjon
import no.nav.dagpenger.inntekt.klassifisering.isLønnHyretillegg
import no.nav.dagpenger.inntekt.klassifisering.isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon
import no.nav.dagpenger.inntekt.klassifisering.isLønnInnbetalingTilUtenlandskPensjonsordning
import no.nav.dagpenger.inntekt.klassifisering.isLønnKilometergodtgjørelseBil
import no.nav.dagpenger.inntekt.klassifisering.isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse
import no.nav.dagpenger.inntekt.klassifisering.isLønnKostDager
import no.nav.dagpenger.inntekt.klassifisering.isLønnKostDøgn
import no.nav.dagpenger.inntekt.klassifisering.isLønnKostbesparelseIHjemmet
import no.nav.dagpenger.inntekt.klassifisering.isLønnLosji
import no.nav.dagpenger.inntekt.klassifisering.isLønnLønnForBarnepassIBarnetsHjem
import no.nav.dagpenger.inntekt.klassifisering.isLønnLønnTilPrivatpersonerForArbeidIHjemmet
import no.nav.dagpenger.inntekt.klassifisering.isLønnLønnTilVergeFraFylkesmannen
import no.nav.dagpenger.inntekt.klassifisering.isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon
import no.nav.dagpenger.inntekt.klassifisering.isLønnOpsjoner
import no.nav.dagpenger.inntekt.klassifisering.isLønnOvertidsgodtgjørelse
import no.nav.dagpenger.inntekt.klassifisering.isLønnOvertidsgodtgjørelseHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnOvertidsgodtgjørelseTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnReiseAnnet
import no.nav.dagpenger.inntekt.klassifisering.isLønnReiseKost
import no.nav.dagpenger.inntekt.klassifisering.isLønnReiseLosji
import no.nav.dagpenger.inntekt.klassifisering.isLønnRentefordelLån
import no.nav.dagpenger.inntekt.klassifisering.isLønnSkattepliktigDelForsikringer
import no.nav.dagpenger.inntekt.klassifisering.isLønnSluttvederlag
import no.nav.dagpenger.inntekt.klassifisering.isLønnSluttvederlagHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnSluttvederlagTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnSmusstillegg
import no.nav.dagpenger.inntekt.klassifisering.isLønnStipend
import no.nav.dagpenger.inntekt.klassifisering.isLønnStyrehonorarOgGodtgjørelseVerv
import no.nav.dagpenger.inntekt.klassifisering.isLønnTimelønn
import no.nav.dagpenger.inntekt.klassifisering.isLønnTimelønnHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnTimelønnTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnTrekkILoennForFerieHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnTrekkILoennForFerieTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnTrekkILønnForFerie
import no.nav.dagpenger.inntekt.klassifisering.isLønnUregelmessigeTilleggKnyttetTilArbeidetTid
import no.nav.dagpenger.inntekt.klassifisering.isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid
import no.nav.dagpenger.inntekt.klassifisering.isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre
import no.nav.dagpenger.inntekt.klassifisering.isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak
import no.nav.dagpenger.inntekt.klassifisering.isLønnYrkebilTjenestligbehovKilometer
import no.nav.dagpenger.inntekt.klassifisering.isLønnYrkebilTjenestligbehovListepris
import no.nav.dagpenger.inntekt.klassifisering.isNæringAnnet
import no.nav.dagpenger.inntekt.klassifisering.isNæringDagpengerTilFisker
import no.nav.dagpenger.inntekt.klassifisering.isNæringLottKunTrygdeavgift
import no.nav.dagpenger.inntekt.klassifisering.isNæringSykepenger
import no.nav.dagpenger.inntekt.klassifisering.isNæringSykepengerTilDagmamma
import no.nav.dagpenger.inntekt.klassifisering.isNæringSykepengerTilFisker
import no.nav.dagpenger.inntekt.klassifisering.isNæringSykepengerTilJordOgSkogbrukere
import no.nav.dagpenger.inntekt.klassifisering.isNæringVederlag
import no.nav.dagpenger.inntekt.klassifisering.isYtelseDagpengerTilFiskerSomBareHarHyre
import no.nav.dagpenger.inntekt.klassifisering.isYtelseDagpengerVedArbeidsløshet
import no.nav.dagpenger.inntekt.klassifisering.isYtelseForeldrepenger
import no.nav.dagpenger.inntekt.klassifisering.isYtelseSvangerskapspenger
import no.nav.dagpenger.inntekt.klassifisering.isYtelseSykepenger
import no.nav.dagpenger.inntekt.klassifisering.isYtelseSykepengerTilFiskerSomBareHarHyre
import java.lang.RuntimeException


//fun mapToStoredInntekt(guiInntekt: GUIInntekt) : StoredInntekt {
//   return StoredInntekt()
//}


fun mapToGUIInntekt(storedInntekt: StoredInntekt): GUIInntekt {
    val mappedInntekt = storedInntekt.inntekt.arbeidsInntektMaaned?.map { arbeidsInntektMaaned ->
        GUIArbeidsInntektMaaned(
            arbeidsInntektMaaned.aarMaaned,
            arbeidsInntektMaaned.avvikListe,
            GUIArbeidsInntektInformasjon(
                arbeidsInntektMaaned.arbeidsInntektInformasjon?.inntektListe?.map {
                    InntektMedKategori(
                        it.beloep,
                        it.fordel,
                        it.beskrivelse,
                        it.inntektskilde,
                        it.inntektsstatus,
                        it.inntektsperiodetype,
                        it.leveringstidspunkt,
                        it.opptjeningsland,
                        it.opptjeningsperiode,
                        it.skattemessigBosattLand,
                        it.utbetaltIMaaned,
                        it.opplysningspliktig,
                        it.inntektsinnsender,
                        it.virksomhet,
                        it.inntektsmottaker,
                        it.inngaarIGrunnlagForTrekk,
                        it.utloeserArbeidsgiveravgift,
                        it.informasjonsstatus,
                        it.inntektType,
                        it.tilleggsinformasjon,
                        mapToKategori(it.inntektType, it.beskrivelse, it.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.spesielleInntjeningsforhold)
                    )
                } ?: emptyList()))
    } ?: emptyList()

    return GUIInntekt(mappedInntekt, storedInntekt.inntekt.ident)
}

fun mapToKategori(
    inntektType: InntektType,
    beskrivelse: InntektBeskrivelse,
    spesielleInntjeningsforhold: SpesielleInntjeningsforhold?
): String {
    val data = DatagrunnlagKlassifisering(inntektType, beskrivelse, spesielleInntjeningsforhold)
    return when {
        isLønnAksjerGrunnfondsbevisTilUnderkurs(data) -> "Aksjer/grunnfondsbevis til underkurs"
        isLønnAnnet(data) -> "Annen arbeidsinntekt"
        isLønnArbeidsoppholdKost(data) -> "Arbeidsopphold kost"
        isLønnArbeidsoppholdLosji(data) -> "Arbeidsopphold losji"
        isLønnBeregnetSkatt(data) -> "Beregnet skatt"
        isLønnBesøksreiserHjemmetAnnet(data) -> "Besøksreiser hjemmet annet"
        isLønnBesøksreiserHjemmetKilometergodtgjørelseBil(data) -> "Besøksreiser hjemmet kilometergodtgjørelse bil"
        isLønnBetaltUtenlandskSkatt(data) -> "Betalt utenlandsk skatt"
        isLønnBil(data) -> "Bil"
        isLønnBolig(data) -> "Bolig"
        isLønnBonus(data) -> "Bonus"
        isLønnBonusFraForsvaret(data) -> "Bonus fra forsvaret"
        isLønnElektroniskKommunikasjon(data) -> "Elektronisk kommunikasjon"
        isLønnFastBilgodtgjørelse(data) -> "Fast bilgodtgjørelse"
        isLønnFastTillegg(data) -> "Faste tillegg"
        isLønnFastlønn(data) -> "Fastlønn"
        isLønnFeriepenger(data) -> "Feriepenger"
        isLønnFondForIdrettsutøvere(data) -> "Fond for idrettsutøvere"
        isYtelseForeldrepenger(data) -> "Foreldrepenger fra folketrygden"
        isLønnHelligdagstillegg(data) -> "Helligdagstillegg"
        isLønnHonorarAkkordProsentProvisjon(data) -> "Honorar, akkord, prosent eller provisjonslønn"
        isLønnHyretillegg(data) -> "Hyretillegg"
        isLønnInnbetalingTilUtenlandskPensjonsordning(data) -> "Innbetaling til utenlandsk pensjonsordning"
        isLønnKilometergodtgjørelseBil(data) -> "Kilometergodtgjørelse bil"
        isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse(data) -> "Kommunal omsorgslønn og fosterhjemsgodtgjørelse"
        isLønnKostDager(data) -> "Kost (dager)"
        isLønnKostDøgn(data) -> "Kost (døgn)"
        isLønnKostbesparelseIHjemmet(data) -> "Kostbesparelse i hjemmet"
        isLønnLosji(data) -> "Losji"
        isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon(data) -> "Lønn mv som ikke er skattepliktig i Norge fra utenlandsk diplomatisk eller konsulær stasjon"
        isLønnLønnForBarnepassIBarnetsHjem(data) -> "Lønn og godtgjørelse til dagmamma eller praktikant som passer barn i barnets hjem"
        isLønnLønnTilPrivatpersonerForArbeidIHjemmet(data) -> "Lønn og godtgjørelse til privatpersoner for arbeidsoppdrag i oppdragsgivers hjem"
        isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon(data) -> "Lønn og godtgjørelse utbetalt av veldedig eller allmennyttig institusjon eller organisasjon"
        isLønnLønnTilVergeFraFylkesmannen(data) -> "Lønn til verge fra Fylkesmannen"
        isLønnOpsjoner(data) -> "Opsjoner"
        isLønnOvertidsgodtgjørelse(data) -> "Overtidsgodtgjørelse"
        isLønnReiseAnnet(data) -> "Reise annet"
        isLønnReiseKost(data) -> "Reise kost"
        isLønnReiseLosji(data) -> "Reise losji"
        isLønnRentefordelLån(data) -> "Rentefordel lån"
        isLønnSkattepliktigDelForsikringer(data) -> "Skattepliktig del av visse typer forsikringer"
        isLønnSluttvederlag(data) -> "Sluttvederlag"
        isLønnSmusstillegg(data) -> "Smusstillegg"
        isLønnStipend(data) -> "Stipend"
        isLønnStyrehonorarOgGodtgjørelseVerv(data) -> "Styrehonorar og godtgjørelse i forbindelse med verv"
        isYtelseSvangerskapspenger(data) -> "Svangerskapspenger"
        isLønnTimelønn(data) -> "Timelønn"
        isLønnTrekkILønnForFerie(data) -> "Trekk i lønn for ferie"
        isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(data) -> "Uregelmessige tillegg knyttet til arbeidet tid"
        isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(data) -> "Uregelmessige tillegg knyttet til ikke-arbeidet tid"
        isLønnYrkebilTjenestligbehovKilometer(data) -> "Yrkebil tjenestligbehov kilometer"
        isLønnYrkebilTjenestligbehovListepris(data) -> "Yrkebil tjenestligbehov listepris"
        isYtelseDagpengerVedArbeidsløshet(data) -> "Dagpenger ved arbeidsløshet"
        isNæringDagpengerTilFisker(data) -> "Dagpenger til fisker"
        isYtelseDagpengerTilFiskerSomBareHarHyre(data) -> "Dagpenger til fisker som bare har hyre"
        isNæringSykepengerTilFisker(data) -> "Sykepenger til fisker"
        isYtelseSykepengerTilFiskerSomBareHarHyre(data) -> "Sykepenger til fisker som bare har hyre"
        isNæringAnnet(data) -> "Annen næringsinntekt"
        isLønnAnnetHyre(data) -> "Hyre - Annet"
        isLønnBonusHyre(data) -> "Hyre - Bonus"
        isLønnFastTilleggHyre(data) -> "Hyre - Faste tillegg"
        isLønnFastlønnHyre(data) -> "Hyre - Fastlønn"
        isLønnFeriepengerHyre(data) -> "Hyre - Feriepenger"
        isLønnHelligdagstilleggHyre(data) -> "Hyre - Helligdagstillegg"
        isLønnOvertidsgodtgjørelseHyre(data) -> "Hyre - Overtidsgodtgjørelse"
        isLønnSluttvederlagHyre(data) -> "Hyre - Sluttvederlag"
        isLønnTimelønnHyre(data) -> "Hyre - Timelønn"
        isLønnUregelmessigeTilleggKnyttetTilArbeidetTidHyre(data) -> "Hyre - Uregelmessige tillegg knyttet til arbeidet tid"
        isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidHyre(data) -> "Hyre - Uregelmessige tillegg knyttet til ikke-arbeidet tid"
        isNæringLottKunTrygdeavgift(data) -> "Lott det skal beregnes trygdeavgift av"
        isLønnTrekkILoennForFerieHyre(data) -> "Trekk i lønn for ferie - Hyre"
        isNæringVederlag(data) -> "Vederlag lott"
        isYtelseSykepenger(data) -> "Sykepenger fra folketrygden"
        isNæringSykepenger(data) -> "Sykepenger fra næring"
        isNæringSykepengerTilDagmamma(data) -> "Sykepenger til dagmamma"
        isNæringSykepengerTilJordOgSkogbrukere(data) -> "Sykepenger til jord- og skogbrukere"
        isLønnAnnetTiltak(data) -> "Tiltak - Annet"
        isLønnBonusTiltak(data) -> "Tiltak - Bonus"
        isLønnFastTilleggTiltak(data) -> "Tiltak - Faste tillegg"
        isLønnFastlønnTiltak(data) -> "Tiltak - Fastlønn"
        isLønnFeriepengerTiltak(data) -> "Tiltak - Feriepenger"
        isLønnHelligdagstilleggTiltak(data) -> "Tiltak - Helligdagstillegg"
        isLønnOvertidsgodtgjørelseTiltak(data) -> "Tiltak - Overtidsgodtgjørelse"
        isLønnSluttvederlagTiltak(data) -> "Tiltak - Sluttvederlag"
        isLønnTimelønnTiltak(data) -> "Tiltak - Timelønn"
        isLønnUregelmessigeTilleggKnyttetTilArbeidetTidTiltak(data) -> "Tiltak - Uregelmessige tillegg knyttet til arbeidet tid"
        isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTidTiltak(data) -> "Tiltak - Uregelmessige tillegg knyttet til ikke-arbeidet tid"
        isLønnTrekkILoennForFerieTiltak(data) -> "Trekk i lønn for ferie - Tiltak"
        else -> throw NoKategoriForInntektFound("Could not find a kategori that satisfies inntektType=$inntektType, beskrivelse=$beskrivelse and spesielleInntjeningsforhold=$spesielleInntjeningsforhold")
    }
}

class NoKategoriForInntektFound(message: String) : RuntimeException(message)

