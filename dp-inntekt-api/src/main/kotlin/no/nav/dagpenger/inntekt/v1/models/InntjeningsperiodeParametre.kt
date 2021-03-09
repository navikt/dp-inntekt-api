package no.nav.dagpenger.inntekt.v1.models

data class InntjeningsperiodeParametre(
    val aktorId: String?, // todo remove
    val vedtakId: Int?, // todo remove
    val beregningsdato: String,
    val inntektsId: String
)
