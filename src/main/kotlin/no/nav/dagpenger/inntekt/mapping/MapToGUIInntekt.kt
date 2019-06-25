package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.klassifisering.DatagrunnlagKlassifisering
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

fun mapToGUIInntekt(inntekt: InntektkomponentResponse, opptjeningsPeriode: Opptjeningsperiode, personnummer: String?) = GUIInntekt(
    null,
    null,
    GUIInntektsKomponentResponse(
        tilDato = opptjeningsPeriode.sisteAvsluttendeKalenderMåned,
        fraDato = opptjeningsPeriode.førsteMåned,
        arbeidsInntektMaaned = mapToArbeidsInntektMaaneder(inntekt.arbeidsInntektMaaned),
        ident = inntekt.ident
    ),
    false,
    personnummer
)

fun mapToGUIInntekt(storedInntekt: StoredInntekt, opptjeningsPeriode: Opptjeningsperiode, personnummer: String?) = GUIInntekt(
    storedInntekt.inntektId,
    storedInntekt.timestamp,
    GUIInntektsKomponentResponse(
        tilDato = opptjeningsPeriode.sisteAvsluttendeKalenderMåned,
        fraDato = opptjeningsPeriode.førsteMåned,
        arbeidsInntektMaaned = mapToArbeidsInntektMaaneder(storedInntekt.inntekt.arbeidsInntektMaaned),
        ident = storedInntekt.inntekt.ident
    ),
    storedInntekt.manueltRedigert,
    personnummer
)

private fun mapToArbeidsInntektMaaneder(list: List<ArbeidsInntektMaaned>?) =
    list?.map { arbeidsInntektMaaned ->
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
    }
