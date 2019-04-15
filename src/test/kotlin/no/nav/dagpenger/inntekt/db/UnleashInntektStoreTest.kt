package no.nav.dagpenger.inntekt.db

import de.huxhorn.sulky.ulid.ULID
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.finn.unleash.Unleash
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UnleashInntektStoreTest {

    private val postgresInntektStore = mockk<InntektStore>(relaxed = true)
    private val voidInntektStore = mockk<InntektStore>(relaxed = true)
    private val request = InntektRequest(
            aktørId = "1245",
            vedtakId = 12345,
            beregningsDato = LocalDate.now()
    )
    private val inntektkomponentResponse = InntektkomponentResponse(
            ident = Aktoer(AktoerType.AKTOER_ID, "12345"),
            arbeidsInntektMaaned = emptyList()
    )

    private val inntektId = InntektId(ULID().nextULID())

    @Test
    fun ` Should direct to postgresInntektStore when toggle is off `() {

        val unleash = mockk<Unleash>()
        every { unleash.isEnabled(any()) } returns false

        val unleashInntektStore = UnleashInntektStore(
                postgresInntektStore = postgresInntektStore,
                voidInntektStore = voidInntektStore,
                unleash = unleash
        )

        unleashInntektStore.insertInntekt(request, inntektkomponentResponse)
        unleashInntektStore.getInntektId(request)
        unleashInntektStore.getInntekt(inntektId)

        verify(ordering = Ordering.ORDERED) {
            postgresInntektStore.insertInntekt(request, inntektkomponentResponse)
            postgresInntektStore.getInntektId(request)
            postgresInntektStore.getInntekt(inntektId)
        }
    }

    @Test
    fun ` Should direct to voidInntektStore when toggle is on `() {

        val unleash = mockk<Unleash>()
        every { unleash.isEnabled(any()) } returns true

        val unleashInntektStore = UnleashInntektStore(
                postgresInntektStore = postgresInntektStore,
                voidInntektStore = voidInntektStore,
                unleash = unleash
        )

        unleashInntektStore.insertInntekt(request, inntektkomponentResponse)
        unleashInntektStore.getInntektId(request)
        unleashInntektStore.getInntekt(inntektId)

        verify(ordering = Ordering.ORDERED) {
            voidInntektStore.insertInntekt(request, inntektkomponentResponse)
            voidInntektStore.getInntektId(request)
            voidInntektStore.getInntekt(inntektId)
        }
    }
}