package no.nav.dagpenger.inntekt.mapping

import no.nav.dagpenger.events.inntekt.v1.Aktør
import no.nav.dagpenger.events.inntekt.v1.AktørType
import no.nav.dagpenger.events.inntekt.v1.Avvik
import no.nav.dagpenger.events.inntekt.v1.InntektId
import no.nav.dagpenger.events.inntekt.v1.MånedsInntekt
import no.nav.dagpenger.events.inntekt.v1.Periode
import no.nav.dagpenger.events.inntekt.v1.Postering
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import java.time.LocalDateTime

fun mapToSpesifisertInntekt(storedInntekt: StoredInntekt) =
    SpesifisertInntekt(
        inntektId = InntektId(storedInntekt.inntektId.id),
        ident = Aktør(
            AktørType.valueOf(storedInntekt.inntekt.ident.aktoerType.toString()),
            storedInntekt.inntekt.ident.identifikator),
        månedsInntekter = mapToMånedsInntekt(storedInntekt.inntekt.arbeidsInntektMaaned),
        manueltRedigert = storedInntekt.manueltRedigert,
        timestamp = storedInntekt.timestamp ?: LocalDateTime.now()
    )

private fun mapToMånedsInntekt(list: List<ArbeidsInntektMaaned>?) =
    list?.map { arbeidsInntektMaaned ->
        MånedsInntekt(
            årMåned = arbeidsInntektMaaned.aarMaaned,
            avvikListe = arbeidsInntektMaaned.avvikListe?.map {
                Avvik(
                    ident = aktoerToAktør(it.ident),
                    opplysningspliktig = aktoerToAktør(it.opplysningspliktig),
                    virksomhet = nullableAktoerToAktør(it.virksomhet),
                    avvikPeriode = it.avvikPeriode,
                    tekst = it.tekst
                )
            } ?: emptyList(),
            posteringer =
                arbeidsInntektMaaned.arbeidsInntektInformasjon?.inntektListe?.map {
                    Postering(
                        beløp = it.beloep,
                        fordel = it.fordel,
                        inntektskilde = it.inntektskilde,
                        inntektsstatus = it.inntektsstatus,
                        inntektsperiodetype = it.inntektsperiodetype,
                        leveringstidspunkt = it.leveringstidspunkt,
                        opptjeningsland = it.opptjeningsland,
                        opptjeningsperiode = it.opptjeningsperiode?.let { periode ->
                            Periode(periode.startDato, periode.sluttDato)
                        },
                        skattemessigBosattLand = it.skattemessigBosattLand,
                        utbetaltIMåned = it.utbetaltIMaaned,
                        opplysningspliktig = nullableAktoerToAktør(it.opplysningspliktig),
                        inntektsinnsender = nullableAktoerToAktør(it.inntektsinnsender),
                        virksomhet = nullableAktoerToAktør(it.virksomhet),
                        inntektsmottaker = nullableAktoerToAktør(it.inntektsmottaker),
                        inngårIGrunnlagForTrekk = it.inngaarIGrunnlagForTrekk,
                        utløserArbeidsgiveravgift = it.utloeserArbeidsgiveravgift,
                        informasjonsstatus = it.informasjonsstatus,
                        posteringsType = toPosteringsType(
                            PosteringsTypeInfo(
                                it.inntektType,
                                it.beskrivelse,
                                it.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.spesielleInntjeningsforhold
                            )
                        )
                    )
                } ?: emptyList())
    } ?: emptyList()

private fun nullableAktoerToAktør(aktoer: Aktoer?): Aktør? =
    aktoer?.let {
        Aktør(AktørType.valueOf(aktoer.aktoerType.toString()), aktoer.identifikator)
    }

private fun aktoerToAktør(aktoer: Aktoer): Aktør =
    Aktør(AktørType.valueOf(aktoer.aktoerType.toString()), aktoer.identifikator)