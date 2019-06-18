package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType

fun predicatesInntektklasseSykepenger(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isYtelseSykepenger
    )
}

fun isYtelseSykepenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER