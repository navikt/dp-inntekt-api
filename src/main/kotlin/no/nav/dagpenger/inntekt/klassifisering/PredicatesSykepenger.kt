package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType

fun predicatesInntektklasseSykepenger(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isYtelseSykepenger,
        ::isNæringSykepenger,
        ::isNæringSykepengerTilDagmamma,
        ::isNæringSykepengerTilJordOgSkogbrukere
    )
}

fun isYtelseSykepenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER

fun isNæringSykepenger(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER

fun isNæringSykepengerTilDagmamma(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER_TIL_DAGMAMMA

fun isNæringSykepengerTilJordOgSkogbrukere(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER_TIL_JORD_OG_SKOGBRUKERE