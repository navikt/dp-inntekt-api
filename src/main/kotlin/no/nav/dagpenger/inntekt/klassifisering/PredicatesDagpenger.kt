package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType

fun predicatesInntektklasseDagpenger(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isYtelseDagpengerVedArbeidsløshet
    )
}

fun isYtelseDagpengerVedArbeidsløshet(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.DAGPENGER_VED_ARBEIDSLOESHET