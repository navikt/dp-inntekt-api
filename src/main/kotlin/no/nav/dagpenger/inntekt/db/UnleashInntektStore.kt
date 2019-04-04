package no.nav.dagpenger.inntekt.db

import no.finn.unleash.Unleash
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest

class UnleashInntektStore(
    val postgresInntektStore: InntektStore,
    val voidInntektStore: InntektStore,
    val unleash: Unleash
) : InntektStore {

    override fun getInntekt(inntektId: InntektId): StoredInntekt {
        return if (unleash.isEnabled("dp-regel-api.PostgresqlStore")) {
            postgresInntektStore.getInntekt(inntektId)
        } else {
            voidInntektStore.getInntekt(inntektId)
        }
    }

    override fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse): StoredInntekt {
        return if (unleash.isEnabled("dp-regel-api.PostgresqlStore")) {
            postgresInntektStore.insertInntekt(request, inntekt)
        } else {
            voidInntektStore.insertInntekt(request, inntekt)
        }
    }

    override fun getInntektId(request: InntektRequest): InntektId? {
        return if (unleash.isEnabled("dp-regel-api.PostgresqlStore")) {
            postgresInntektStore.getInntektId(request)
        } else {
            voidInntektStore.getInntektId(request)
        }
    }
}