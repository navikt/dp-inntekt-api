package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import kotlin.reflect.KFunction

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
        else -> throw Exception()
    }
}

//fun mapToStoredInntekt(guiInntekt: GUIInntekt) : StoredInntekt {
//   return StoredInntekt()
//}

//fun kategoriserInntekt(inntektkomponentResponse: InntektkomponentResponse):

// todo - TEST THIS!!! MISSING UNIT TEST

fun klassifiserInntekter(uklassifiserteInntekter: InntektkomponentResponse): List<KlassifisertInntektMåned> {

    return uklassifiserteInntekter.arbeidsInntektMaaned?.map { måned ->
        val årMåned = måned.aarMaaned
        val avvik = måned.avvikListe?.map { avvik -> Pair(avvik.ident, avvik.avvikPeriode) } ?: emptyList()
        val inntektSplitt: InntektSplitt = måned.arbeidsInntektInformasjon
            ?.inntektListe
            ?.partition { inntekt -> avvik.any { avvik -> avvik.first == inntekt.inntektsmottaker && avvik.second == inntekt.utbetaltIMaaned } }
        val klassifiserteInntekter = inntektSplitt?.second?.map { inntekt ->
            val datagrunnlagKlassifisering = DatagrunnlagKlassifisering(
                inntekt.inntektType, inntekt.beskrivelse, inntekt.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.spesielleInntjeningsforhold
            )

            val inntektKlasse = klassifiserInntekt(datagrunnlagKlassifisering)
            KlassifisertInntekt(inntekt.beloep, inntektKlasse)
        } ?: emptyList()
        KlassifisertInntektMåned(årMåned, inntektSplitt?.first?.isNotEmpty(), klassifiserteInntekter)
    } ?: emptyList()
}

private fun klassifiserInntekt(datagrunnlag: DatagrunnlagKlassifisering): InntektKlasse {
    val inntektKlassePredicates = listOf(
        predicatesInntektklasseArbeid() to InntektKlasse.ARBEIDSINNTEKT,
        predicatesInntektklasseDagpenger() to InntektKlasse.DAGPENGER,
        predicatesInntektklasseDagpengerFangstFiske() to InntektKlasse.DAGPENGER_FANGST_FISKE,
        predicatesInntektklasseNæringsinntekt() to InntektKlasse.FANGST_FISKE,
        predicatesInntektklasseSykepenger() to InntektKlasse.SYKEPENGER,
        predicatesInntektklasseSykepengerFangstFiske() to InntektKlasse.SYKEPENGER_FANGST_FISKE,
        predicatesInntektklasseTiltakslønn() to InntektKlasse.TILTAKSLØNN
    )

    val klasser = inntektKlassePredicates
        .filter { (predicates, _) -> matchesSingularPredicate(datagrunnlag, predicates) }
        .map { (_, klasse) -> klasse }

    return when {
        klasser.size > 1 -> throw KlassifiseringException("$datagrunnlag klassifisert til flere klasser: $klasser")
        klasser.isEmpty() -> throw KlassifiseringException("Fant ingen klasse til $datagrunnlag")
        else -> klasser.first()
    }
}

fun matchesSingularPredicate(
    datagrunnlag: DatagrunnlagKlassifisering,
    predicates: List<(DatagrunnlagKlassifisering) -> Boolean>
): Boolean {

    val matchingPredicates = predicates.filter { predicate -> predicate(datagrunnlag) }

    if (matchingPredicates.size > 1) {
        throw MultipleMatchingPredicatesException(
            "Multiple matching predicates: ${matchingPredicates.map { functionName(it) }}. " +
                "Predicates: ${predicates.map { functionName(it) }}."
        )
    }

    return matchingPredicates.size == 1
}

fun <T, V> functionName(function: (V) -> T): String {
    val functionAsKFunction: KFunction<*> = function as KFunction<*>
    return functionAsKFunction.name
}

data class DatagrunnlagKlassifisering(
    val type: InntektType,
    val beskrivelse: InntektBeskrivelse,
    val forhold: SpesielleInntjeningsforhold? = null
)

class KlassifiseringException(override val message: String) : RuntimeException(message)

class MultipleMatchingPredicatesException(override val message: String) : RuntimeException(message)

typealias InntektSplitt = Pair<List<no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt>, List<no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt>>?