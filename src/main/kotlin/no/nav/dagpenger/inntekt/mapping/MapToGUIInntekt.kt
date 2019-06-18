package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.klassifisering.DatagrunnlagKlassifisering
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

fun mapToGUIInntekt(storedInntekt: StoredInntekt, opptjeningsPeriode: Opptjeningsperiode): GUIInntekt {
    val mappedInntekt = storedInntekt.inntekt.arbeidsInntektMaaned?.map { arbeidsInntektMaaned ->
        GUIArbeidsInntektMaaned(
            arbeidsInntektMaaned.aarMaaned,
            arbeidsInntektMaaned.avvikListe,
            GUIArbeidsInntektInformasjon(
                arbeidsInntektMaaned.arbeidsInntektInformasjon?.inntektListe?.map {
                    InntektMedVerdikode(
                        it.beloep,
                        it.fordel,
                        it.beskrivelse,
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
                        it.inntektType,
                        it.tilleggsinformasjon,
                        verdiKode(
                            DatagrunnlagKlassifisering(
                                it.inntektType,
                                it.beskrivelse,
                                it.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.spesielleInntjeningsforhold
                            )
                        )
                    )
                } ?: emptyList()))
    } ?: emptyList()

    return GUIInntekt(
        storedInntekt.inntektId,
        GUIInntektsKomponentResponse(
            tilDato = opptjeningsPeriode.sisteAvsluttendeKalenderMåned,
            fraDato = opptjeningsPeriode.førsteMåned,
            arbeidsInntektMaaned = mappedInntekt,
            ident = storedInntekt.inntekt.ident
        ),
        storedInntekt.manueltRedigert
    )
}
