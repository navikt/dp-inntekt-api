package no.nav.dagpenger.inntekt

import no.nav.dagpenger.events.inntekt.v1.Inntekt
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.Inntektparametre
import no.nav.dagpenger.inntekt.db.StoreInntektCommand
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.klassifiserer.klassifiserOgMapInntekt
import no.nav.dagpenger.inntekt.mapping.mapToSpesifisertInntekt

class BehandlingsInntektsGetter(
    private val inntektskomponentClient: InntektskomponentClient,
    private val inntektStore: InntektStore
) {

    suspend fun getKlassifisertInntekt(inntektparametre: Inntektparametre): Inntekt {
        return klassifiserOgMapInntekt(getSpesifisertInntekt(inntektparametre))
    }

    suspend fun getSpesifisertInntekt(inntektparametre: Inntektparametre): SpesifisertInntekt {
        return mapToSpesifisertInntekt(getBehandlingsInntekt(inntektparametre), inntektparametre.opptjeningsperiode.sisteAvsluttendeKalenderMåned)
    }

    internal suspend fun getBehandlingsInntekt(inntektparametre: Inntektparametre): StoredInntekt {
        return isInntektStored(inntektparametre)?.let { inntektStore.getInntekt(it) }
            ?: fetchAndStoreInntekt(inntektparametre)
    }

    private suspend fun fetchAndStoreInntekt(
        inntektparametre: Inntektparametre
    ): StoredInntekt {
        val inntektkomponentRequest = InntektkomponentRequest(
            inntektparametre.aktørId,
            inntektparametre.opptjeningsperiode.førsteMåned,
            inntektparametre.opptjeningsperiode.sisteAvsluttendeKalenderMåned
        )
        return inntektStore.storeInntekt(
            StoreInntektCommand(
                inntektparametre = inntektparametre,
                inntekt = inntektskomponentClient.getInntekt(inntektkomponentRequest)
            )

        )
    }

    private fun isInntektStored(inntektparametre: Inntektparametre) = inntektStore.getInntektId(inntektparametre)
}
