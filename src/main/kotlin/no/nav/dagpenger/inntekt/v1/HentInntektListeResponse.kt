package no.nav.dagpenger.inntekt.v1

import com.squareup.moshi.Json
import java.math.BigDecimal
import java.time.YearMonth

data class HentInntektListeResponse(
    val arbeidsInntektMaaned: List<ArbeidsInntektMaaned>?,
    val ident: Aktoer
)

data class ArbeidsInntektMaaned(
    val aarMaaned: YearMonth,
    val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
)

data class ArbeidsInntektInformasjon(
    val inntektListe: List<Inntekt>
)

data class Inntekt(
    val beloep: BigDecimal,
    val beskrivelse: InntektBeskrivelse,
    val fordel: String,
    val informasjonsstatus: String,
    val inngaarIGrunnlagForTrekk: Boolean,
    val inntektType: InntektType,
    val inntektskilde: String,
    val inntektsmottaker: Aktoer,
    val inntektsperiodetype: String,
    val inntektsstatus: String,
    val leveringstidspunkt: YearMonth,
    val opplysningspliktig: Aktoer,
    val utbetaltIMaaned: YearMonth,
    val utloeserArbeidsgiveravgift: Boolean,
    val virksomhet: Aktoer
)

data class Aktoer(
    val aktoerType: AktoerType,
    val identifikator: String
)

enum class AktoerType {
    AKTOER_ID,
    NATURLIG_IDENT,
    ORGANISASJON
}

enum class InntektType {
    LOENNSINNTEKT,
    NAERINGSINNTEKT,
    PENSJON_ELLER_TRYGD,
    YTELSE_FRA_OFFENTLIGE
}

enum class SpesielleInntjeningsforhold {
    @Json(name = "hyreTilMannskapPaaFiskeSmaahvalfangstOgSelfangstfartoey") HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY,
    @Json(name = "loennVedArbeidsmarkedstiltak") LOENN_VED_ARBEIDSMARKEDSTILTAK,
    UNKNOWN
}

enum class InntektBeskrivelse {
    @Json(name = "aksjerGrunnfondsbevisTilUnderkurs") AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS,
    @Json(name = "annet") ANNET,
    @Json(name = "arbeidsoppholdKost") ARBEIDSOPPHOLD_KOST,
    @Json(name = "arbeidsoppholdLosji") ARBEIDSOPPHOLD_LOSJI,
    @Json(name = "beregnetSkatt") BEREGNET_SKATT,
    @Json(name = "besoeksreiserHjemmetAnnet") BESOEKSREISER_HJEMMET_ANNET,
    @Json(name = "besoeksreiserHjemmetKilometergodtgjoerelseBil") BESOEKSREISER_HJEMMET_KILOMETERGODTGJOERELSE_BIL,
    @Json(name = "betaltUtenlandskSkatt") BETALT_UTENLANDSK_SKATT,
    @Json(name = "bil") BIL,
    @Json(name = "bolig") BOLIG,
    @Json(name = "bonus") BONUS,
    @Json(name = "bonusFraForsvaret") BONUS_FRA_FORSVARET,
    @Json(name = "elektroniskKommunikasjon") ELEKTRONISK_KOMMUNIKASJON,
    @Json(name = "fastBilgodtgjoerelse") FAST_BILGODTGJOERELSE,
    @Json(name = "fastTillegg") FAST_TILLEGG,
    @Json(name = "fastloenn") FASTLOENN,
    @Json(name = "feriepenger") FERIEPENGER,
    @Json(name = "fondForIdrettsutoevere") FOND_FOR_IDRETTSUTOEVERE,
    @Json(name = "foreldrepenger") FORELDREPENGER,
    @Json(name = "helligdagstillegg") HELLIGDAGSTILLEGG,
    @Json(name = "honorarAkkordProsentProvisjon") HONORAR_AKKORD_PROSENT_PROVISJON,
    @Json(name = "hyretillegg") HYRETILLEGG,
    @Json(name = "innbetalingTilUtenlandskPensjonsordning") INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING,
    @Json(name = "kilometergodtgjoerelseBil") KILOMETERGODTGJOERELSE_BIL,
    @Json(name = "kommunalOmsorgsloennOgFosterhjemsgodtgjoerelse") KOMMUNAL_OMSORGSLOENN_OG_FOSTERHJEMSGODTGJOERELSE,
    @Json(name = "kostDager") KOST_DAGER,
    @Json(name = "kostDoegn") KOST_DOEGN,
    @Json(name = "kostbesparelseIHjemmet") KOSTBESPARELSE_I_HJEMMET,
    @Json(name = "losji") LOSJI,
    @Json(name = "ikkeSkattepliktigLoennFraUtenlandskDiplomKonsulStasjon") IKKE_SKATTEPLIKTIG_LOENN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON,
    @Json(name = "loennForBarnepassIBarnetsHjem") LOENN_FOR_BARNEPASS_I_BARNETS_HJEM,
    @Json(name = "loennTilPrivatpersonerForArbeidIHjemmet") LOENN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET,
    @Json(name = "loennUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon") LOENN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON,
    @Json(name = "loennTilVergeFraFylkesmannen") LOENN_TIL_VERGE_FRA_FYLKESMANNEN,
    @Json(name = "opsjoner") OPSJONER,
    @Json(name = "overtidsgodtgjoerelse") OVERTIDSGODTGJOERELSE,
    @Json(name = "reiseAnnet") REISE_ANNET,
    @Json(name = "reiseKost") REISE_KOST,
    @Json(name = "reiseLosji") REISE_LOSJI,
    @Json(name = "rentefordelLaan") RENTEFORDEL_LAAN,
    @Json(name = "skattepliktigDelForsikringer") SKATTEPLIKTIG_DEL_FORSIKRINGER,
    @Json(name = "sluttvederlag") SLUTTVEDERLAG,
    @Json(name = "smusstillegg") SMUSSTILLEGG,
    @Json(name = "stipend") STIPEND,
    @Json(name = "styrehonorarOgGodtgjoerelseVerv") STYREHONORAR_OG_GODTGJOERELSE_VERV,
    @Json(name = "svangerskapspenger") SVANGERSKAPSPENGER,
    @Json(name = "timeloenn") TIMELOENN,
    @Json(name = "trekkILoennForFerie") TREKK_I_LOENN_FOR_FERIE,
    @Json(name = "uregelmessigeTilleggKnyttetTilArbeidetTid") UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
    @Json(name = "uregelmessigeTilleggKnyttetTilIkkeArbeidetTid") UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
    @Json(name = "yrkebilTjenestligbehovKilometer") YRKEBIL_TJENESTLIGBEHOV_KILOMETER,
    @Json(name = "yrkebilTjenestligbehovListepris") YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS,
    @Json(name = "dagpengerVedArbeidsloeshet") DAGPENGER_VED_ARBEIDSLOESHET,
    @Json(name = "dagpengerTilFisker") DAGPENGER_TIL_FISKER,
    @Json(name = "dagpengerTilFiskerSomBareHarHyre") DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE,
    @Json(name = "sykepengerTilFisker") SYKEPENGER_TIL_FISKER,
    @Json(name = "sykepengerTilFiskerSomBareHarHyre") SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE,
    @Json(name = "lottKunTrygdeavgift") LOTT_KUN_TRYGDEAVGIFT,
    @Json(name = "vederlag") VEDERLAG,
    @Json(name = "sykepenger") SYKEPENGER,
    @Json(name = "sykepengerTilDagmamma") SYKEPENGER_TIL_DAGMAMMA,
    @Json(name = "sykepengerTilJordOgSkogbrukere") SYKEPENGER_TIL_JORD_OG_SKOGBRUKERE,
}
