package no.nav.dagpenger.inntekt

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
    val beskrivelse: String,
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
    val utbetaltIMaaned: String,
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