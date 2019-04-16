package no.nav.dagpenger.inntekt.db

import no.finn.unleash.Unleash
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest
import java.time.LocalDate

class UnleashInntektStore(
    val postgresInntektStore: InntektStore,
    val voidInntektStore: InntektStore,
    val unleash: Unleash
) : InntektStore {

    private val useVoidStore = "dp-inntekt-api.UseVoidStore"

    override fun getInntekt(inntektId: InntektId): StoredInntekt {
        return if (unleash.isEnabled(useVoidStore)) {
            voidInntektStore.getInntekt(inntektId)
        } else {
            postgresInntektStore.getInntekt(inntektId)
        }
    }

    override fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse): StoredInntekt {
        return if (unleash.isEnabled(useVoidStore)) {
            voidInntektStore.insertInntekt(request, inntekt)
        } else {
            postgresInntektStore.insertInntekt(request, inntekt)
        }
    }

    override fun getInntektId(request: InntektRequest): InntektId? {
        return if (unleash.isEnabled(useVoidStore)) {
            voidInntektStore.getInntektId(request)
        } else {
            postgresInntektStore.getInntektId(request)
        }
    }

    override fun getBeregningsdato(inntektId: InntektId): LocalDate {
        return if (unleash.isEnabled(useVoidStore)) {
            voidInntektStore.getBeregningsdato(inntektId)
        } else {
            postgresInntektStore.getBeregningsdato(inntektId)
        }
    }
}