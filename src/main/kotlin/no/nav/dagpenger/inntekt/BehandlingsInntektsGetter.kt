package no.nav.dagpenger.inntekt

import de.huxhorn.sulky.ulid.ULID
import java.time.LocalDateTime
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

class BehandlingsInntektsGetter(
    private val inntektskomponentClient: InntektskomponentClient,
    private val inntektStore: InntektStore
) {
    private val ulidGenerator = ULID()

    suspend fun getBehandlingsInntekt(behandlingsKey: BehandlingsKey): StoredInntekt {
        val opptjeningsperiode = Opptjeningsperiode(behandlingsKey.beregningsDato)

        val inntektkomponentRequest = InntektkomponentRequest(
            behandlingsKey.aktørId,
            opptjeningsperiode.førsteMåned,
            opptjeningsperiode.sisteAvsluttendeKalenderMåned
        )

        if (hasVedtakAssociation(behandlingsKey)) {
            return isInntektStored(behandlingsKey)?.let { inntektStore.getInntekt(it) }
                ?: fetchAndStoreInntekt(behandlingsKey, inntektkomponentRequest)
        }

        return getUnstoredInntekt(inntektkomponentRequest)
    }

    private suspend fun fetchAndStoreInntekt(
        behandlingsKey: BehandlingsKey,
        inntektkomponentRequest: InntektkomponentRequest
    ): StoredInntekt {
        return inntektStore.insertInntekt(
            behandlingsKey,
            inntektskomponentClient.getInntekt(inntektkomponentRequest)
        )
    }

    private fun isInntektStored(behandlingsKey: BehandlingsKey) =
        inntektStore.getInntektId(behandlingsKey)

    private suspend fun getUnstoredInntekt(inntektkomponentRequest: InntektkomponentRequest): StoredInntekt {
        return StoredInntekt(
            inntektId = InntektId(ulidGenerator.nextULID()),
            inntekt = inntektskomponentClient.getInntekt(inntektkomponentRequest),
            manueltRedigert = false,
            timestamp = LocalDateTime.now()
        )
    }

    private fun hasVedtakAssociation(behandlingsKey: BehandlingsKey) =
        behandlingsKey.vedtakId != null
}