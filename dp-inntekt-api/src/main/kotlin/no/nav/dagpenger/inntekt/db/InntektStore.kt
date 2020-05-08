package no.nav.dagpenger.inntekt.db

import de.huxhorn.sulky.ulid.ULID
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse

interface InntektStore {
    fun getInntekt(inntektId: InntektId): StoredInntekt
    fun getInntektId(inntektparametre: Inntektparametre): InntektId?
    fun getBeregningsdato(inntektId: InntektId): LocalDate
    fun storeInntekt(command: StoreInntektCommand, created: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)): StoredInntekt
    fun getManueltRedigert(inntektId: InntektId): ManueltRedigert?
    fun markerInntektBrukt(inntektId: InntektId): Int
}

data class Inntektparametre(
    val aktørId: String,
    val vedtakId: String,
    val beregningsdato: LocalDate,
    val fødselnummer: String? = null
) {
    internal fun migrateCandidate(): Boolean = kotlin.runCatching { vedtakId.toLong() }.isSuccess
}

data class StoreInntektCommand(
    val inntektparametre: Inntektparametre,
    val inntekt: InntektkomponentResponse,
    val manueltRedigert: ManueltRedigert? = null
)

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