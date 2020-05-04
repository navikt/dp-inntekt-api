package no.nav.dagpenger.inntekt.db

import de.huxhorn.sulky.ulid.ULID
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeExactly
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.random.Random
import kotliquery.LoanPattern.using
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.dagpenger.inntekt.DataSource as TestDataSource
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.withMigratedDb
import org.junit.jupiter.api.Test
import org.postgresql.util.PGobject

internal class ArenaMappingMigratorTest {

    companion object {

        private val numberOfInserts = 100
        private val hentInntektListeResponse = InntektkomponentResponse(
            emptyList(),
            Aktoer(AktoerType.AKTOER_ID, "1234")
        )

        private val inntektIds = (1..numberOfInserts).toList().map { InntektId(ULID().nextULID()) to StoreInntektCommand(
            inntektparametre = Inntektparametre(Random.nextInt().toString(), Random.nextLong().toString(), LocalDate.now()),
            inntekt = hentInntektListeResponse
        ) }
    }

    @Test
    fun `Skal migrere data fra arena mapping til person tabell `() {
        withMigratedDb {
            fillArenaMappingTable(TestDataSource.instance)
            val arenaMappingMigrator = ArenaMappingMigrator(TestDataSource.instance)
            val rowsMigrated = arenaMappingMigrator.migrate()

            val inntektStore = PostgresInntektStore(TestDataSource.instance)

            rowsMigrated shouldBeExactly numberOfInserts

            val ids = inntektIds.mapNotNull { (_, command) ->
                inntektStore.getInntektId(
                    Inntektparametre(
                        aktørId = command.inntektparametre.aktørId,
                        vedtakId = command.inntektparametre.vedtakId,
                        beregningsdato = command.inntektparametre.beregningsdato
                    )
                )
            }

            ids shouldContainAll inntektIds.map { it.first }
        }
    }

    private fun fillArenaMappingTable(dataSource: javax.sql.DataSource) {
        inntektIds.forEach { (id, command) ->
            using(sessionOf(dataSource)) { session ->
                session.transaction { tx ->
                    tx.run(
                        queryOf(
                            "INSERT INTO inntekt_V1 (id, inntekt, manuelt_redigert, timestamp) VALUES (:id, :data, :manuelt, :created)",
                            mapOf(
                                "id" to id.id,
                                "created" to ZonedDateTime.now(),
                                "data" to PGobject().apply {
                                    type = "jsonb"
                                    value = PostgresInntektStore.adapter.toJson(command.inntekt)
                                },
                                when (command.manueltRedigert) {
                                    null -> "manuelt" to false
                                    else -> "manuelt" to true
                                }
                            )

                        ).asUpdate
                    )
                    tx.run(
                        queryOf(
                            "INSERT INTO inntekt_V1_arena_mapping VALUES (:inntektId, :aktorId, :vedtakId, :beregningsDato)",
                            mapOf(
                                "inntektId" to id.id,
                                "aktorId" to command.inntektparametre.aktørId,
                                "vedtakId" to command.inntektparametre.vedtakId.toLong(),
                                "beregningsDato" to command.inntektparametre.beregningsdato
                            )
                        ).asUpdate
                    )
                }
            }
        }
    }
}
