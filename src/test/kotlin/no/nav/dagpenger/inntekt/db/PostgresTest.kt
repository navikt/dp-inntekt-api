package no.nav.dagpenger.inntekt.db

import com.zaxxer.hikari.HikariDataSource
import no.nav.dagpenger.inntekt.Configuration

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentenResponse
import no.nav.dagpenger.inntekt.v1.InntektRequest
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PostgresTest {

    @Test
    fun `Migration scripts are applied successfully`() {
        withCleanDb {
            val migrations = migrate(DataSource.instance)
            assertEquals(1, migrations, "Wrong number of migrations")
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
    fun `JDBC url is set correctly from  config values `() {
        with(hikariConfigFrom(Configuration())) {
            assertEquals("jdbc:postgresql://localhost:5432/dp-inntekt-db", jdbcUrl)
        }
    }
}

internal class PostgresInntektStoreTest {
    @Test
    fun `Successful insert of inntekter`() {
        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val request = InntektRequest("1234", 1234, java.time.LocalDate.now())
                val hentInntektListeResponse = InntektkomponentenResponse(
                        emptyList(),
                        Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                val storedInntekt = insertInntekt(request, hentInntektListeResponse)
                assertNotNull(storedInntekt.id)
                assertTrue("Inntekstliste should be in the same state") { hentInntektListeResponse == storedInntekt.inntekt }

                val storedInntektByRequest = getInntekt(request)
                assertTrue("Inntekstliste should be in the same state") { storedInntekt == storedInntektByRequest }
            }
        }
    }

    @Test
    fun ` Sucessfully get inntekter`() {

        withMigratedDb {
            with(PostgresInntektStore(DataSource.instance)) {
                val hentInntektListeResponse = InntektkomponentenResponse(
                        emptyList(),
                        Aktoer(AktoerType.AKTOER_ID, "1234")
                )
                insertInntekt(InntektRequest("1234", 12345, java.time.LocalDate.now()), hentInntektListeResponse)

                val storedInntekt = getInntekt(InntektRequest("1234", 12345, java.time.LocalDate.now()))
                assertNotNull(storedInntekt.id)
                assertTrue("Inntekstliste should be in the same state") { hentInntektListeResponse == storedInntekt.inntekt }
            }
        }
    }
}

private fun withCleanDb(test: () -> Unit) = DataSource.instance.also { clean(it) }.run { test() }

private fun withMigratedDb(test: () -> Unit) = DataSource.instance.also { clean(it) }.also { migrate(it) }.run { test() }

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