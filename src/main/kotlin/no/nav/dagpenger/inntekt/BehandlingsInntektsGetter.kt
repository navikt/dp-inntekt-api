package no.nav.dagpenger.inntekt

import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.Inntektparametre
import no.nav.dagpenger.inntekt.db.StoreInntektCommand
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.opptjeningsperiode.Opptjeningsperiode

class BehandlingsInntektsGetter(
    private val inntektskomponentClient: InntektskomponentClient,
    private val inntektStore: InntektStore
) {

    suspend fun getBehandlingsInntekt(inntektparametre: Inntektparametre): StoredInntekt {
        val opptjeningsperiode = Opptjeningsperiode(inntektparametre.beregningsdato)

        val inntektkomponentRequest = InntektkomponentRequest(
            inntektparametre.aktørId,
            opptjeningsperiode.førsteMåned,
            opptjeningsperiode.sisteAvsluttendeKalenderMåned
        )

        return isInntektStored(inntektparametre)?.let { inntektStore.getInntekt(it) }
            ?: fetchAndStoreInntekt(inntektparametre, inntektkomponentRequest)
    }

    private suspend fun fetchAndStoreInntekt(
        inntektparametre: Inntektparametre,
        inntektkomponentRequest: InntektkomponentRequest
    ): StoredInntekt {
        return inntektStore.storeInntekt(
            StoreInntektCommand(
                inntektparametre = inntektparametre,
                inntekt = inntektskomponentClient.getInntekt(inntektkomponentRequest)
            )

        )
    }

    private fun isInntektStored(inntektparametre: Inntektparametre) = inntektStore.getInntektId(inntektparametre)
}
