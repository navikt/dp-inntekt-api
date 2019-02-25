package no.nav.dagpenger.inntekt.v1.klassifisering

import java.math.BigDecimal
import java.time.YearMonth

data class KlassifisertInntektListe(
    val inntektsId: String,
    val inntektsListe: List<KlassifisertInntektMåned>
)

data class KlassifisertInntektMåned(
    val årMåned: YearMonth,
    val klassifiserteInntekter: List<KlassifisertInntekt>
)

data class KlassifisertInntekt(
    val beløp: BigDecimal,
    val inntektKlasse: InntektKlasse
)

enum class InntektKlasse {
    ARBEIDSINNTEKT,
    DAGPENGER,
    DAGPENGER_FANGST_FISK,
    SYKEPENGER_FANGST_FISK,
    NÆRINGSINNTEKT,
    FØDSELSPENGER,
    SYKEPENGER,
    TILTAKSLØNN
}