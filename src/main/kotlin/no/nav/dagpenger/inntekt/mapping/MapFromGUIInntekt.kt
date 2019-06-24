package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.inntekt.db.DetachedInntekt
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjonsDetaljer
import no.nav.dagpenger.inntekt.klassifisering.DatagrunnlagKlassifisering
import java.lang.IllegalArgumentException

fun mapFromGUIInntekt(guiInntekt: GUIInntekt): StoredInntekt {
    val unMappedInntekt = hubba(guiInntekt) ?: emptyList()
    return guiInntekt.inntektId?.let {
        StoredInntekt(guiInntekt.inntektId, InntektkomponentResponse(unMappedInntekt, guiInntekt.inntekt.ident), guiInntekt.manueltRedigert)
    } ?: throw IllegalArgumentException("missing innktektId")
}

fun mapTo(guiInntekt: GUIInntekt): DetachedInntekt {
    val unMappedInntekt = hubba(guiInntekt) ?: emptyList()
    return DetachedInntekt(InntektkomponentResponse(unMappedInntekt, guiInntekt.inntekt.ident), guiInntekt.manueltRedigert)
}

private fun hubba(guiInntekt: GUIInntekt): List<ArbeidsInntektMaaned>? {
    return guiInntekt.inntekt.arbeidsInntektMaaned?.map { GUIarbeidsInntektMaaned ->
        ArbeidsInntektMaaned(
            GUIarbeidsInntektMaaned.aarMaaned,
            GUIarbeidsInntektMaaned.avvikListe,
            ArbeidsInntektInformasjon(
                GUIarbeidsInntektMaaned.arbeidsInntektInformasjon?.inntektListe?.map { inntekt ->
                    val datagrunnlagForVerdikode: DatagrunnlagKlassifisering = dataGrunnlag(inntekt.verdikode)
                    Inntekt(
                        inntekt.beloep,
                        inntekt.fordel ?: "",
                        datagrunnlagForVerdikode.beskrivelse,
                        inntekt.inntektskilde,
                        inntekt.inntektsstatus ?: "LoependeInnrapportert",
                        inntekt.inntektsperiodetype ?: "Maaned",
                        inntekt.leveringstidspunkt,
                        inntekt.opptjeningsland,
                        inntekt.opptjeningsperiode,
                        inntekt.skattemessigBosattLand,
                        inntekt.utbetaltIMaaned,
                        inntekt.opplysningspliktig,
                        inntekt.inntektsinnsender,
                        inntekt.virksomhet,
                        inntekt.inntektsmottaker,
                        inntekt.inngaarIGrunnlagForTrekk,
                        inntekt.utloeserArbeidsgiveravgift,
                        inntekt.informasjonsstatus,
                        datagrunnlagForVerdikode.type,
                        datagrunnlagForVerdikode.forhold?.let { TilleggInformasjon(inntekt.tilleggsinformasjon?.kategori, TilleggInformasjonsDetaljer(inntekt.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.detaljerType, it)) }
                    )
                } ?: emptyList()))
    }
}
