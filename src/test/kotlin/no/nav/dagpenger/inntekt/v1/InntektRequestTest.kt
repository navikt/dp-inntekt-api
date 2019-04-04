package no.nav.dagpenger.inntekt.v1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class InntektRequestTest {
    @Test
    fun ` Map to InntektkomponentenRequest`() {
        val request = InntektRequest(
                aktørId = "1234",
                vedtakId = 1223,
                beregningsDato = LocalDate.of(2019, 4, 3)
        )

        val inntektskomponentRequest = request.let(TO_INNTEKTKOMPONENT_REQUEST)

        assertEquals("1234", inntektskomponentRequest.aktørId)
        assertEquals("2016-02", inntektskomponentRequest.månedFom.toString())
        assertEquals("2019-04", inntektskomponentRequest.månedTom.toString())
    }
}