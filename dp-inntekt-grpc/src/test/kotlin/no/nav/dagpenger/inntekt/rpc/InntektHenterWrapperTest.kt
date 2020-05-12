package no.nav.dagpenger.inntekt.rpc

import com.squareup.moshi.JsonAdapter
import de.huxhorn.sulky.ulid.ULID
import io.grpc.Status
import io.grpc.StatusException
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.spyk
import java.time.LocalDateTime
import java.time.YearMonth
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.events.inntekt.v1.Aktør
import no.nav.dagpenger.events.inntekt.v1.AktørType
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.inntekt.rpc.InntektHenterGrpcKt.InntektHenterCoroutineImplBase
import org.junit.Test
import org.junit.jupiter.api.assertThrows

internal class InntektHenterWrapperTest : GrpcTest() {

    companion object {
        val spesifisertInntektAdapter: JsonAdapter<SpesifisertInntekt> =
            moshiInstance.adapter(SpesifisertInntekt::class.java)
    }
    private val serverMock = spyk<InntektHenterCoroutineImplBase>()

    @Test
    fun ` Should expose StatusException errors `() {

        val inntektId = "1233"
        every { runBlocking { serverMock.hentSpesifisertInntektAsJson(InntektId.newBuilder().setId(inntektId).build()) } } throws StatusException(Status.INVALID_ARGUMENT)
        val client = InntektHenterClient(makeChannel(serverMock))
        val exc = assertThrows<StatusException> { runBlocking { client.hentSpesifisertInntekt(inntektId) } }
        exc.status.code shouldBe Status.INVALID_ARGUMENT.code
    }

    @Test
    fun ` Should get spesifisert inntekt `() {

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

        every {
            runBlocking {
                serverMock.hentSpesifisertInntektAsJson(
                    InntektId.newBuilder().setId(inntektId).build()
                )
            }
        } returns SpesifisertInntektAsJson.newBuilder()
            .setInntektId(InntektId.newBuilder().setId(inntektId).build())
            .setJson(spesifisertInntektAdapter.toJson(spesifisertInntekt)).build()

        val client = InntektHenterClient(makeChannel(serverMock))

        val response = runBlocking { client.hentSpesifisertInntekt(inntektId) }
        response shouldBe spesifisertInntekt
    }
}
