package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType

fun predicatesInntektklasseDagpengerFangstFiske(): List<(DatagrunnlagKlassifisering) -> Boolean> {
    return listOf(
        ::isNæringDagpengerTilFisker,
        ::isYtelseDagpengerTilFiskerSomBareHarHyre
    )
}

fun isNæringDagpengerTilFisker(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.NAERINGSINNTEKT && datagrunnlag.beskrivelse == InntektBeskrivelse.DAGPENGER_TIL_FISKER

fun isYtelseDagpengerTilFiskerSomBareHarHyre(datagrunnlag: DatagrunnlagKlassifisering): Boolean =
    datagrunnlag.type == InntektType.YTELSE_FRA_OFFENTLIGE && datagrunnlag.beskrivelse == InntektBeskrivelse.DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE