package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Avvik
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Periode
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import java.math.BigDecimal
import java.time.YearMonth

data class Inntekt(
    val inntektsId: String,
    val inntektsListe: List<KlassifisertInntektMåned>,
    val manueltRedigert: Boolean,
    val sisteAvsluttendeKalenderMåned: YearMonth
)

data class KlassifisertInntektMåned(
    val årMåned: YearMonth,
    val harAvvik: Boolean?,
    val klassifiserteInntekter: List<KlassifisertInntekt>
)

data class KlassifisertInntekt(
    val beløp: BigDecimal,
    val inntektKlasse: InntektKlasse
)

enum class InntektKlasse {
    ARBEIDSINNTEKT,
    DAGPENGER,
    DAGPENGER_FANGST_FISKE,
    SYKEPENGER_FANGST_FISKE,
    FANGST_FISKE,
    SYKEPENGER,
    TILTAKSLØNN
}

data class GUIInntekt(
    val arbeidsInntektMaaned: List<GUIArbeidsInntektMaaned>?,
    val ident: Aktoer
)

data class GUIArbeidsInntektMaaned(
    val aarMaaned: YearMonth,
    val avvikListe: List<Avvik>?,
    val arbeidsInntektInformasjon: GUIArbeidsInntektInformasjon?
)

data class GUIArbeidsInntektInformasjon(
    val inntektListe: List<InntektMedKategori>?
)

data class InntektMedKategori(
    val beloep: BigDecimal,
    val fordel: String,
    val beskrivelse: InntektBeskrivelse,
    val inntektskilde: String,
    val inntektsstatus: String,
    val inntektsperiodetype: String,
    val leveringstidspunkt: YearMonth? = null,
    val opptjeningsland: String? = null,
    val opptjeningsperiode: Periode? = null,
    val skattemessigBosattLand: String? = null,
    val utbetaltIMaaned: YearMonth,
    val opplysningspliktig: Aktoer? = null,
    val inntektsinnsender: Aktoer? = null,
    val virksomhet: Aktoer? = null,
    val inntektsmottaker: Aktoer? = null,
    val inngaarIGrunnlagForTrekk: Boolean? = null,
    val utloeserArbeidsgiveravgift: Boolean? = null,
    val informasjonsstatus: String? = null,
    val inntektType: InntektType,
    val tilleggsinformasjon: TilleggInformasjon? = null,
    val kategori: String
)

