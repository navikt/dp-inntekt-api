package no.nav.dagpenger.inntekt

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode
import no.nav.dagpenger.inntekt.v1.models.Inntekt
import java.time.LocalDateTime

class BehandlingsInntektsGetter(
    private val inntektskomponentClient: InntektskomponentClient,
    private val inntektStore: InntektStore
) {
    private val ulidGenerator = ULID()

    suspend fun getBehandlingsInntekt(behandlingsKey: BehandlingsKey): Inntekt {
        val opptjeningsperiode = Opptjeningsperiode(behandlingsKey.beregningsDato)

        val inntektkomponentRequest = InntektkomponentRequest(
            behandlingsKey.aktørId,
            opptjeningsperiode.førsteMåned,
            opptjeningsperiode.sisteAvsluttendeKalenderMåned
        )

        if (!hasVedtakAssociation(behandlingsKey)) {
            return getUnassociatedInntekt(inntektkomponentRequest)
        }

        return getAssociatedInntekt(behandlingsKey, inntektkomponentRequest)
    }

    private suspend fun getAssociatedInntekt(
        behandlingsKey: BehandlingsKey,
        inntektkomponentRequest: InntektkomponentRequest
    ): Inntekt {
        val storedInntekt = (isInntektStored(behandlingsKey)?.let { inntektStore.getInntekt(it) }
            ?: fetchAndStoreInntekt(behandlingsKey, inntektkomponentRequest))

        return Inntekt(
            storedInntekt.inntektId,
            storedInntekt.inntekt,
            storedInntekt.manueltRedigert,
            storedInntekt.timestamp
        )
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

    private suspend fun getUnassociatedInntekt(inntektkomponentRequest: InntektkomponentRequest): Inntekt {
        return Inntekt(
            inntektId = InntektId(ulidGenerator.nextULID()),
            inntekt = inntektskomponentClient.getInntekt(inntektkomponentRequest),
            manueltRedigert = false,
            timestamp = LocalDateTime.now()
        )
    }

    private fun hasVedtakAssociation(behandlingsKey: BehandlingsKey) =
        behandlingsKey.vedtakId != null
}