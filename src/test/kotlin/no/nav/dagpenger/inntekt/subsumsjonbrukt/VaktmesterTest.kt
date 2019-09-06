package no.nav.dagpenger.inntekt.subsumsjonbrukt

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.DataSource
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.PostgresInntektStore
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.withMigratedDb
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime

internal class VaktmesterTest {

    private val behandlingsKey = BehandlingsKey(
        aktørId = "1234",
        vedtakId = 1234,
        beregningsDato = LocalDate.now()
    )

    private val inntekter = InntektkomponentResponse(
        ident = Aktoer(AktoerType.AKTOER_ID, behandlingsKey.aktørId),
        arbeidsInntektMaaned = emptyList()
    )

    @Test
    fun `Skal ikke slette brukte inntekter`() {

        withMigratedDb {
            val inntektStore = PostgresInntektStore(DataSource.instance)
            val bruktInntekt = inntektStore.insertInntekt(
                request = behandlingsKey,
                inntekt = inntekter
            )
            inntektStore.markerInntektBrukt(bruktInntekt.inntektId)
            val vaktmester = Vaktmester(DataSource.instance)
            vaktmester.rydd()
            inntektStore.getInntekt(bruktInntekt.inntektId) shouldNotBe null
        }
    }

    @Test
    fun `Skal kun slette inntekt som ikke er brukt selvom det er referrert til samme behandlingsnøkler som en annen inntekt som er brukt`() {
        withMigratedDb {
            val inntektStore = PostgresInntektStore(DataSource.instance)
            val ubruktInntekt = inntektStore.insertInntekt(
                request = behandlingsKey,
                inntekt = inntekter,
                created = ZonedDateTime.now().minusMonths(4)
            )
            val bruktInntekt = inntektStore.insertInntekt(
                request = behandlingsKey,
                inntekt = inntekter.copy(
                    arbeidsInntektMaaned = listOf(
                        ArbeidsInntektMaaned(
                            aarMaaned = YearMonth.now(),
                            arbeidsInntektInformasjon = ArbeidsInntektInformasjon(emptyList()),
                            avvikListe = emptyList()
                        )
                    )
                ),
                created = ZonedDateTime.now().minusMonths(4)
            )
            inntektStore.markerInntektBrukt(bruktInntekt.inntektId)
            val vaktmester = Vaktmester(DataSource.instance)
            vaktmester.rydd()
            inntektStore.getInntektId(behandlingsKey) shouldBe bruktInntekt.inntektId
            assertThrows<InntektNotFoundException> { inntektStore.getInntekt(ubruktInntekt.inntektId) }
        }
    }

    @Test
    fun `Skal kun slette ubrukte inntekter som er eldre enn 90 dager`() {
        withMigratedDb {
            val inntektStore = PostgresInntektStore(DataSource.instance)
            val ubruktEldreEnn90Dager = inntektStore.insertInntekt(
                request = behandlingsKey,
                inntekt = inntekter,
                created = ZonedDateTime.now().minusMonths(4)
            )
            val ubruktYngreEnn90Dager = inntektStore.insertInntekt(
                request = behandlingsKey,
                inntekt = inntekter
            )

            val vaktmester = Vaktmester(DataSource.instance)
            vaktmester.rydd()
            assertThrows<InntektNotFoundException> { inntektStore.getInntekt(ubruktEldreEnn90Dager.inntektId) }
            inntektStore.getInntekt(ubruktYngreEnn90Dager.inntektId) shouldBe ubruktYngreEnn90Dager
        }
    }
}