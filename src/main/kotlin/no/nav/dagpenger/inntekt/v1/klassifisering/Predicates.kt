package no.nav.dagpenger.inntekt.v1.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType

fun inntektklasseArbeidPredicates(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isLønnFastlønn,
        ::isLønnFeriepenger
    )
}

// TODO: fix SpesielleInntjeningsforhold
fun isLønnFastlønn(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FASTLOENN

// TODO: fix SpesielleInntjeningsforhold
fun isLønnFeriepenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.LOENNSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.FERIEPENGER