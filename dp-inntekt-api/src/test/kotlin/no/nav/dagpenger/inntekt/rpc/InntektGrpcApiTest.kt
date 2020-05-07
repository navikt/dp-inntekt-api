package no.nav.dagpenger.inntekt.rpc

import de.huxhorn.sulky.ulid.ULID
import io.grpc.Status
import io.grpc.StatusException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import org.junit.Test
import org.junit.jupiter.api.assertThrows

internal class InntektGrpcApiTest : GrpcTest() {

    @Test
    fun `Should throw error on illegal inntekt id`() {
        val inntektStore = mockk<InntektStore>(relaxed = true).also {
            every { it.getInntekt(any()) } throws InntektNotFoundException("Not found")
        }
        val client = InntektHenterGrpcKt.InntektHenterCoroutineStub(makeChannel(InntektGrpcApi(inntektStore)))
        val request = InntektId.newBuilder().setId("1233").build()

        val exception = assertThrows<StatusException> { runBlocking { client.hentInntekt(request) } }
        exception.status.code shouldBe Status.INVALID_ARGUMENT.code
    }

    @Test
    fun `Should throw error when inntekt do not exist`() {
        val inntektStore = mockk<InntektStore>(relaxed = true).also {
            every { it.getInntekt(any()) } throws InntektNotFoundException("Not found")
        }
        val client = InntektHenterGrpcKt.InntektHenterCoroutineStub(makeChannel(InntektGrpcApi(inntektStore)))
        val request = InntektId.newBuilder().setId(ULID().nextULID()).build()

        val exception = assertThrows<StatusException> { runBlocking { client.hentInntekt(request) } }
        exception.status.code shouldBe Status.NOT_FOUND.code
    }

    @Test
    fun `Should fetch inntekt `() {
        val inntektId = ULID().nextULID()
        val inntektStore = mockk<InntektStore>(relaxed = true).also {
            every { it.getInntekt(any()) } returns StoredInntekt(
                inntektId = no.nav.dagpenger.inntekt.db.InntektId(inntektId),
                inntekt = InntektkomponentResponse(
                    arbeidsInntektMaaned = emptyList(),
                    ident = Aktoer(AktoerType.AKTOER_ID, "1234")
                ),
                manueltRedigert = false,
                timestamp = LocalDateTime.now()
            )
        }
        val client = InntektHenterGrpcKt.InntektHenterCoroutineStub(makeChannel(InntektGrpcApi(inntektStore)))
        val request = InntektId.newBuilder().setId(inntektId).build()

        val inntekt = runBlocking { client.hentInntekt(request) }
        inntekt.inntektId.id shouldBe inntektId
        inntekt.json shouldNotBe null
    }
}
