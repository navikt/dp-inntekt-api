package no.nav.dagpenger.inntekt

import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

class BehandlingsInntektsGetter(private val inntektskomponentClient: InntektskomponentClient, private val inntektStore: InntektStore) {

    suspend fun getBehandlingsInntekt(behandlingsKey: BehandlingsKey): StoredInntekt {

        val opptjeningsperiode = Opptjeningsperiode(behandlingsKey.beregningsDato)

        val inntektkomponentRequest = InntektkomponentRequest(
            behandlingsKey.aktørId,
            opptjeningsperiode.førsteMåned,
            opptjeningsperiode.sisteAvsluttendeKalenderMåned
        )

        val storedInntekt = inntektStore.getInntektId(behandlingsKey)?.let { inntektStore.getInntekt(it) }
            ?: inntektStore.insertInntekt(
                behandlingsKey,
                inntektskomponentClient.getInntekt(inntektkomponentRequest)
            )

        return storedInntekt
    }
}