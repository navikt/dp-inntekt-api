package no.nav.dagpenger.inntekt.v1

import com.squareup.moshi.Json
import java.math.BigDecimal
import java.time.YearMonth

data class HentInntektListeResponse(
    val arbeidsInntektMaaned: List<ArbeidsInntektMaaned>,
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
    @Json(name = "fastloenn") FASTLOENN,
    @Json(name = "feriepenger") FERIEPENGER
}