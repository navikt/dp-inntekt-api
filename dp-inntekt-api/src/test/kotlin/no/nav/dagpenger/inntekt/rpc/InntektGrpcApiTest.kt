package no.nav.dagpenger.inntekt.rpc

import de.huxhorn.sulky.ulid.ULID
import io.grpc.Status
import io.grpc.StatusException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.YearMonth
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.events.inntekt.v1.Aktør
import no.nav.dagpenger.events.inntekt.v1.AktørType
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.moshiInstance
import org.junit.Test
import org.junit.jupiter.api.assertThrows

internal class InntektGrpcApiTest : GrpcTest() {

    @Test
    fun `Should throw error on illegal inntekt id`() {
        val inntektStore = mockk<InntektStore>(relaxed = true).also {
            every { it.getSpesifisertInntekt(any()) } throws InntektNotFoundException("Not found")
        }
        val client =
            SpesifisertInntektHenterGrpcKt.SpesifisertInntektHenterCoroutineStub(makeChannel(InntektGrpcApi(inntektStore)))
        val request = InntektId.newBuilder().setId("1233").build()

        val exception = assertThrows<StatusException> { runBlocking { client.hentSpesifisertInntektAsJson(request) } }
        exception.status.code shouldBe Status.INVALID_ARGUMENT.code
    }

    @Test
    fun `Should throw error when inntekt do not exist`() {
        val inntektStore = mockk<InntektStore>(relaxed = true).also {
            every { it.getSpesifisertInntekt(any()) } throws InntektNotFoundException("Not found")
        }
        val client =
            SpesifisertInntektHenterGrpcKt.SpesifisertInntektHenterCoroutineStub(makeChannel(InntektGrpcApi(inntektStore)))
        val request = InntektId.newBuilder().setId(ULID().nextULID()).build()

        val exception = assertThrows<StatusException> { runBlocking { client.hentSpesifisertInntektAsJson(request) } }
        exception.status.code shouldBe Status.NOT_FOUND.code
    }

    @Test
    fun `Should fetch inntekt `() {
        val inntektId = ULID().nextULID()
        val spesifisertInntekt = SpesifisertInntekt(
            inntektId = no.nav.dagpenger.events.inntekt.v1.InntektId(inntektId),
            avvik = emptyList(),
            posteringer = emptyList(),
            sisteAvsluttendeKalenderMåned = YearMonth.now(),
            ident = Aktør(AktørType.AKTOER_ID, "1234"),
            manueltRedigert = false,
            timestamp = LocalDateTime.now()
        )

        val inntektStore = mockk<InntektStore>(relaxed = true).also {
            every { it.getSpesifisertInntekt(any()) } returns spesifisertInntekt
        }
        val client =
            SpesifisertInntektHenterGrpcKt.SpesifisertInntektHenterCoroutineStub(makeChannel(InntektGrpcApi(inntektStore)))
        val request = InntektId.newBuilder().setId(inntektId).build()

        val inntekt = runBlocking { client.hentSpesifisertInntektAsJson(request) }
        inntekt.inntektId.id shouldBe inntektId
        inntekt.json shouldNotBe null
        val spesifisertInntektResponse = moshiInstance.adapter(SpesifisertInntekt::class.java).fromJson(inntekt.json)!!
        spesifisertInntekt shouldBe spesifisertInntektResponse
    }
}
