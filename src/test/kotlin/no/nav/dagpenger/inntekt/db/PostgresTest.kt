package no.nav.dagpenger.inntekt.db

import com.zaxxer.hikari.HikariDataSource
import no.nav.dagpenger.inntekt.Configuration
import no.nav.dagpenger.inntekt.dummyConfigs
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest
import no.nav.dagpenger.inntekt.withProps
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class PostgresTest {

    @Test
    fun `Migration scripts are applied successfully`() {
        withCleanDb {
            val migrations = migrate(DataSource.instance)
            assertEquals(3, migrations, "Wrong number of migrations")
        }
    }

    @Test
    fun `Migration scripts are idempotent`() {
        withCleanDb {
            migrate(DataSource.instance)

            val migrations = migrate(DataSource.instance)
            assertEquals(0, migrations, "Wrong number of migrations")
        }
    }

    @Test
    fun `Migration of testdata `() {
        withCleanDb {
            val migrations = migrate(DataSource.instance, locations = listOf("db/migration", "db/testdata"))
            assertEquals(7, migrations, "Wrong number of migrations")
        }
    }

    @Test
    fun `JDBC url is set correctly from  config values `() {
        withProps(dummyConfigs) {
            with(hikariConfigFrom(Configuration())) {
                assertEquals("jdbc:postgresql://localhost:5432/dp-inntekt-db", jdbcUrl)
            }
        }
    }
}

internal class PostgresInntektStoreTest {
    @Test
    fun `Successful insert of inntekter`() {
        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val request = InntektRequest("1234", 1234, LocalDate.now())
                val hentInntektListeResponse = InntektkomponentResponse(
                    emptyList(),
                    Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                val storedInntekt = insertInntekt(request, hentInntektListeResponse)
                assertNotNull(storedInntekt.inntektId)
                assertTrue("Inntekstliste should be in the same state") { hentInntektListeResponse == storedInntekt.inntekt }

                val storedInntektByRequest = getInntekt(storedInntekt.inntektId)
                assertTrue("Inntekstliste should be in the same state") { storedInntekt == storedInntektByRequest }
            }
        }
    }

    @Test
    fun ` Sucessfully get inntekter`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val hentInntektListeResponse = InntektkomponentResponse(
                    emptyList(),
                    Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                insertInntekt(InntektRequest("1234", 12345, LocalDate.now()), hentInntektListeResponse)

                val inntektId = getInntektId(InntektRequest("1234", 12345, LocalDate.now()))
                val storedInntekt = inntektId?.let { getInntekt(it) }!!
                assertNotNull(storedInntekt.inntektId)
                assertTrue("Inntekstliste should be in the same state") { hentInntektListeResponse == storedInntekt.inntekt }
                assertFalse("Inntekt is manually edited") { storedInntekt.manueltRedigert }
            }
        }
    }

    @Test
    fun `Successful rediger of inntekter`() {
        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val request = InntektRequest("1234", 1234, LocalDate.now())
                val hentInntektListeResponse = InntektkomponentResponse(
                    emptyList(),
                    Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                val storedInntekt = insertInntekt(request, hentInntektListeResponse)
                assertNotNull(storedInntekt.inntektId)
                assertTrue("Inntekstliste should be in the same state") { hentInntektListeResponse == storedInntekt.inntekt }

                val updatedInntekt = redigerInntekt(storedInntekt.copy())

                val storedInntektByRequest = getInntekt(updatedInntekt.inntektId)
                assertTrue("Inntekstliste should be in the same state") { updatedInntekt == storedInntektByRequest }
                assertTrue("Inntekt is manually edited") { storedInntektByRequest.manueltRedigert }
            }
        }
    }

    @Test
    fun ` Inntekt not present should give null StoredInntekt`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val inntektId = getInntektId(InntektRequest("7890", 7890, LocalDate.now()))
                assertNull(inntektId)
            }
        }
    }

    @Test
    fun `getInntektId should return latest InntektId`() {
        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val hentInntektListeResponse = InntektkomponentResponse(
                    emptyList(),
                    Aktoer(AktoerType.AKTOER_ID, "1234")
                )

                val inntektRequest = InntektRequest("1234", 12345, LocalDate.now())

                insertInntekt(inntektRequest, hentInntektListeResponse)
                val lastStoredInntekt = insertInntekt(inntektRequest, hentInntektListeResponse)

                val latestInntektId = getInntektId(inntektRequest)

                assertEquals(lastStoredInntekt.inntektId, latestInntektId)
            }
        }
    }

    @Test
    fun ` Sucessfully get beregningsdato`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val hentInntektListeResponse = InntektkomponentResponse(
                    emptyList(),
                    Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                val inntekt = insertInntekt(
                    InntektRequest("1234", 12345, LocalDate.of(2019, 4, 14)),
                    hentInntektListeResponse
                )

                val beregningsdato = getBeregningsdato(inntekt.inntektId)

                assertNotNull(beregningsdato)
                assertEquals(LocalDate.of(2019, 4, 14), beregningsdato)
            }
        }
    }

    @Test
    fun ` Getting beregningsdato for unknown inntektId should throw error`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val result = runCatching {
                    getBeregningsdato(InntektId("12ARZ3NDEKTSV4RRFFQ69G5FBY"))
                }
                assertTrue("Result is not failure") { result.isFailure }
                assertTrue("Result is $result") { result.exceptionOrNull() is InntektNotFoundException }
            }
        }
    }

    @Test
    fun ` Sucessfully get compound key`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val hentInntektListeResponse = InntektkomponentResponse(
                    emptyList(),
                    Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                val inntekt = insertInntekt(
                    InntektRequest("1234", 12345, LocalDate.of(2019, 4, 14)),
                    hentInntektListeResponse
                )

                val compoundKey = getInntektCompoundKey(inntekt.inntektId)

                assertNotNull(compoundKey)
                assertEquals("1234", compoundKey.aktørId)
                assertEquals(12345, compoundKey.vedtakId)
                assertEquals(LocalDate.of(2019, 4, 14), compoundKey.beregningsDato)
            }
        }
    }

    @Test
    fun ` Getting compound key for unknown inntektId should throw error`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val result = runCatching {
                    getInntektCompoundKey(InntektId("12ARZ3NDEKTSV4RRFFQ69G5FBY"))
                }
                assertTrue("Result is not failure") { result.isFailure }
                assertTrue("Result is $result") { result.exceptionOrNull() is InntektNotFoundException }
            }
        }
    }
}

private fun withCleanDb(test: () -> Unit) = DataSource.instance.also { clean(it) }.run { test() }

private fun withMigratedDb(test: () -> Unit) =
    DataSource.instance.also { clean(it) }.also { migrate(it) }.run { test() }

private object PostgresContainer {
    val instance by lazy {
        PostgreSQLContainer<Nothing>("postgres:11.2").apply {
            start()
        }
    }
}

private object DataSource {
    val instance: HikariDataSource by lazy {
        HikariDataSource().apply {
            username = PostgresContainer.instance.username
            password = PostgresContainer.instance.password
            jdbcUrl = PostgresContainer.instance.jdbcUrl
            connectionTimeout = 1000L
        }
    }
}