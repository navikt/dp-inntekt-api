package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjonsDetaljer

fun mapFromGUIInntekt(guiInntekt: GUIInntekt) : StoredInntekt {
    val unMappedInntekt = guiInntekt.inntekt.arbeidsInntektMaaned?.map { GUIarbeidsInntektMaaned ->
        ArbeidsInntektMaaned(
            GUIarbeidsInntektMaaned.aarMaaned,
            GUIarbeidsInntektMaaned.avvikListe,
            ArbeidsInntektInformasjon(
                GUIarbeidsInntektMaaned.arbeidsInntektInformasjon?.inntektListe?.map {
                    val datagrunnlagForVerdikode = dataGrunnlag(it.verdikode)
                    Inntekt(
                        it.beloep,
                        it.fordel,
                        datagrunnlagForVerdikode.beskrivelse,
                        it.inntektskilde,
                        it.inntektsstatus,
                        it.inntektsperiodetype,
                        it.leveringstidspunkt,
                        it.opptjeningsland,
                        it.opptjeningsperiode,
                        it.skattemessigBosattLand,
                        it.utbetaltIMaaned,
                        it.opplysningspliktig,
                        it.inntektsinnsender,
                        it.virksomhet,
                        it.inntektsmottaker,
                        it.inngaarIGrunnlagForTrekk,
                        it.utloeserArbeidsgiveravgift,
                        it.informasjonsstatus,
                        datagrunnlagForVerdikode.type,
                        datagrunnlagForVerdikode.forhold?.let { TilleggInformasjon("SpesielleInntjeningsforhold", TilleggInformasjonsDetaljer("INNTJENINGSFORHOLD", it)) }
                    )
                } ?: emptyList()))
    } ?: emptyList()
    return StoredInntekt(guiInntekt.inntektId, InntektkomponentResponse(unMappedInntekt, guiInntekt.inntekt.ident), guiInntekt.manueltRedigert)
}

