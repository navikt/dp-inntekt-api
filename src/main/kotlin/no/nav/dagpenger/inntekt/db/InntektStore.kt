package no.nav.dagpenger.inntekt.db

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest
import java.time.LocalDate

interface InntektStore {
    fun getInntekt(inntektId: InntektId): StoredInntekt
    fun getInntektId(request: InntektRequest): InntektId?
    fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse): StoredInntekt
    fun redigerInntekt(redigertInntekt: StoredInntekt): StoredInntekt
    fun getBeregningsdato(inntektId: InntektId): LocalDate
    fun getInntektCompoundKey(inntektId: InntektId): InntektCompoundKey
}

data class StoredInntekt(val inntektId: InntektId, val inntekt: InntektkomponentResponse, val manueltRedigert: Boolean)

data class InntektId(val id: String) {
    init {
        try {
            ULID.parseULID(id)
        } catch (e: IllegalArgumentException) {
            throw IllegalInntektIdException("ID $id is not a valid ULID", e)
        }
    }
}

data class InntektCompoundKey(
    val aktørId: String,
    val vedtakId: Long,
    val beregningsDato: LocalDate
)

class InntektNotFoundException(override val message: String) : RuntimeException(message)

class StoreException(override val message: String) : RuntimeException(message)

class IllegalInntektIdException(override val message: String, override val cause: Throwable?) : java.lang.RuntimeException(message, cause)
