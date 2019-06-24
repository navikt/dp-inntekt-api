package no.nav.dagpenger.inntekt.db

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest
import java.time.LocalDate
import java.time.LocalDateTime

interface InntektStore {
    fun getInntekt(inntektId: InntektId): StoredInntekt
    fun getInntektId(request: InntektRequest): InntektId?
    fun getBeregningsdato(inntektId: InntektId): LocalDate
    fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse, manueltRedigert: Boolean): StoredInntekt
    fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse): StoredInntekt
}

interface Inntekt

data class StoredInntekt(val inntektId: InntektId, val inntekt: InntektkomponentResponse, val manueltRedigert: Boolean, val timestamp: LocalDateTime? = null) : Inntekt
data class DetachedInntekt(val inntekt: InntektkomponentResponse, val manueltRedigert: Boolean) : Inntekt

data class InntektId(val id: String) {
    init {
        try {
            ULID.parseULID(id)
        } catch (e: IllegalArgumentException) {
            throw IllegalInntektIdException("ID $id is not a valid ULID", e)
        }
    }
}

class InntektNotFoundException(override val message: String) : RuntimeException(message)

class StoreException(override val message: String) : RuntimeException(message)

class IllegalInntektIdException(override val message: String, override val cause: Throwable?) : java.lang.RuntimeException(message, cause)
