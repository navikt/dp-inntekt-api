package no.nav.dagpenger.inntekt.db

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

interface InntektStore {
    fun getInntekt(inntektId: InntektId): StoredInntekt
    fun getInntektId(request: BehandlingsKey): InntektId?
    fun getBeregningsdato(inntektId: InntektId): LocalDate
    fun insertInntekt(request: BehandlingsKey, inntekt: InntektkomponentResponse, manueltRedigert: ManueltRedigert? = null, created: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)): StoredInntekt
    fun getManueltRedigert(inntektId: InntektId): ManueltRedigert?
    fun markerInntektBrukt(inntektId: InntektId): Int
}

data class ManueltRedigert(val redigertAv: String) {
    companion object {
        fun from(bool: Boolean, redigertAv: String) = when (bool) {
            true -> ManueltRedigert(redigertAv)
            false -> null
        }
    }
}

data class StoredInntekt(
    val inntektId: InntektId,
    val inntekt: InntektkomponentResponse,
    val manueltRedigert: Boolean,
    val timestamp: LocalDateTime? = null
)
data class DetachedInntekt(val inntekt: InntektkomponentResponse, val manueltRedigert: Boolean)

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
