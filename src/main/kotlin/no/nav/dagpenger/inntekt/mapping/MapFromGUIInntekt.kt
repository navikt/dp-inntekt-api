package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjonsDetaljer

fun mapFromGUIInntekt(guiInntekt: GUIInntekt): StoredInntekt {
    val unMappedInntekt = guiInntekt.inntekt.arbeidsInntektMaaned?.map { GUIarbeidsInntektMaaned ->
        ArbeidsInntektMaaned(
            GUIarbeidsInntektMaaned.aarMaaned,
            GUIarbeidsInntektMaaned.avvikListe,
            ArbeidsInntektInformasjon(
                GUIarbeidsInntektMaaned.arbeidsInntektInformasjon?.inntektListe?.map { inntekt ->
                    val datagrunnlagForVerdikode = dataGrunnlag(inntekt.verdikode)
                    Inntekt(
                        inntekt.beloep,
                        inntekt.fordel,
                        datagrunnlagForVerdikode.beskrivelse,
                        inntekt.inntektskilde,
                        inntekt.inntektsstatus,
                        inntekt.inntektsperiodetype,
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
    } ?: emptyList()
    return StoredInntekt(guiInntekt.inntektId, InntektkomponentResponse(unMappedInntekt, guiInntekt.inntekt.ident), guiInntekt.manueltRedigert)
}
