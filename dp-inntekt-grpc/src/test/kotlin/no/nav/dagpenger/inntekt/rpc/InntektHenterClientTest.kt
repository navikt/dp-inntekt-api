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
import no.nav.dagpenger.events.inntekt.v1.Inntekt
import no.nav.dagpenger.events.inntekt.v1.InntektKlasse
import no.nav.dagpenger.events.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.events.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.inntekt.rpc.InntektHenterGrpcKt.InntektHenterCoroutineImplBase
import org.junit.Test
import org.junit.jupiter.api.assertThrows

internal class InntektHenterClientTest : GrpcTest() {

    companion object {
        val spesifisertInntektAdapter: JsonAdapter<SpesifisertInntekt> =
            moshiInstance.adapter(SpesifisertInntekt::class.java)
        val klassifisertInntektAdapter: JsonAdapter<Inntekt> =
            moshiInstance.adapter(Inntekt::class.java)
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

    @Test
    fun ` Should get klassifisert inntekt `() {

        val inntektId = ULID().nextULID()
        val inntekt = Inntekt(
            inntektsId = inntektId,
            inntektsListe = listOf(
                KlassifisertInntektMåned(
                    årMåned = YearMonth.now(),
                    klassifiserteInntekter = listOf(
                        KlassifisertInntekt(
                            beløp = 1000.toBigDecimal(),
                            inntektKlasse = InntektKlasse.ARBEIDSINNTEKT
                        )
                    ),
                    harAvvik = true
                )
            ),
            manueltRedigert = false,
            sisteAvsluttendeKalenderMåned = YearMonth.now()
        )

        every {
            runBlocking {
                serverMock.hentKlassifisertInntektAsJson(
                    InntektId.newBuilder().setId(inntektId).build()
                )
            }
        } returns KlassifisertInntektAsJson.newBuilder()
            .setInntektId(InntektId.newBuilder().setId(inntektId).build())
            .setJson(klassifisertInntektAdapter.toJson(inntekt)).build()

        val client = InntektHenterClient(makeChannel(serverMock))

        val response = runBlocking { client.hentKlassifisertInntekt(inntektId) }
        response.inntektsId shouldBe inntekt.inntektsId
        response.inntektsListe shouldBe inntekt.inntektsListe
        response.manueltRedigert shouldBe inntekt.manueltRedigert
        response.sisteAvsluttendeKalenderMåned shouldBe inntekt.sisteAvsluttendeKalenderMåned
    }
}
