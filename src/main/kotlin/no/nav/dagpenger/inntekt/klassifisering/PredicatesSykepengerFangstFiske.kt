package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType

fun predicatesInntektklasseSykepengerFangstFiske(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isNæringSykepengerTilFisker,
        ::isYtelseSykepengerTilFiskerSomBareHarHyre
    )
}

fun isNæringSykepengerTilFisker(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER_TIL_FISKER

fun isYtelseSykepengerTilFiskerSomBareHarHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE