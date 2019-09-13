package no.nav.dagpenger.inntekt

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentRequest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

class CachedInntektsGetterTest {

    private val inntektskomponentClientMock: InntektskomponentClient = mockk()
    private val inntektStoreMock: InntektStore = mockk()

    private val emptyInntektsKomponentResponse = InntektkomponentResponse(
        emptyList(),
        Aktoer(AktoerType.AKTOER_ID, "1234")
    )

    @Test
    fun `Get cached inntekt for known behandlingsKey`() {
        val knownBehandlingsKey = BehandlingsKey(
            "1234",
            112233,
            LocalDate.of(2019, 5, 6)
        )

        val knownStoredInntekt = StoredInntekt(
            InntektId("01DGPKY3CCN8H87WNTBK696TKV"),
            emptyInntektsKomponentResponse,
            false
        )

        every {
            inntektStoreMock.getInntektId(knownBehandlingsKey)
        } returns knownStoredInntekt.inntektId

        every {
            inntektStoreMock.getInntekt(knownStoredInntekt.inntektId)
        } returns knownStoredInntekt

        val cachedInntektsGetter = BehandlingsInntektsGetter(inntektskomponentClientMock, inntektStoreMock)
        val storedInntektResult = runBlocking { cachedInntektsGetter.getBehandlingsInntekt(knownBehandlingsKey) }

        verify(exactly = 0) { runBlocking { inntektskomponentClientMock.getInntekt(any()) } }
        assertEquals(emptyInntektsKomponentResponse.ident, storedInntektResult.inntekt.ident)
    }

    @Test
    fun `Get new inntekt for uknown behandlingsKey`() {
        val unknownBehandlingsKey = BehandlingsKey(
            "5678",
            546787,
            LocalDate.of(2019, 4, 26)
        )

        every {
            runBlocking {
                inntektskomponentClientMock.getInntekt(
                    InntektkomponentRequest(
                        "5678",
                        YearMonth.of(2016, 4),
                        YearMonth.of(2019, 3)
                    )
                )
            }
        } returns emptyInntektsKomponentResponse

        every {
            inntektStoreMock.getInntektId(unknownBehandlingsKey)
        } returns null

        every {
            inntektStoreMock.insertInntekt(unknownBehandlingsKey, emptyInntektsKomponentResponse)
        } returns StoredInntekt(InntektId("01DH179R2HW0FYEP1FABAXV150"), emptyInntektsKomponentResponse, false)

        val cachedInntektsGetter = BehandlingsInntektsGetter(inntektskomponentClientMock, inntektStoreMock)
        val storedInntektResult = runBlocking { cachedInntektsGetter.getBehandlingsInntekt(unknownBehandlingsKey) }

        verify(exactly = 1) { runBlocking { inntektskomponentClientMock.getInntekt(any()) } }
        verify(exactly = 1) { runBlocking { inntektStoreMock.insertInntekt(any(), any()) } }
        assertEquals(emptyInntektsKomponentResponse.ident, storedInntektResult.inntekt.ident)
    }
}