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

    private val toggle = "dp-inntekt-api.PostgresqlStore"

    override fun getInntekt(inntektId: InntektId): StoredInntekt {
        return if (unleash.isEnabled(toggle)) {
            postgresInntektStore.getInntekt(inntektId)
        } else {
            voidInntektStore.getInntekt(inntektId)
        }
    }

    override fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse): StoredInntekt {
        return if (unleash.isEnabled(toggle)) {
            postgresInntektStore.insertInntekt(request, inntekt)
        } else {
            voidInntektStore.insertInntekt(request, inntekt)
        }
    }

    override fun getInntektId(request: InntektRequest): InntektId? {
        return if (unleash.isEnabled(toggle)) {
            postgresInntektStore.getInntektId(request)
        } else {
            voidInntektStore.getInntektId(request)
        }
    }

    override fun getBeregningsdato(inntektId: InntektId): LocalDate {
        return if (unleash.isEnabled(toggle)) {
            postgresInntektStore.getBeregningsdato(inntektId)
        } else {
            voidInntektStore.getBeregningsdato(inntektId)
        }
    }
}