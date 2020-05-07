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

internal class ArenaMappingMigratorTest() {

    companion object {

        private val hentInntektListeResponse = InntektkomponentResponse(
            emptyList(),
            Aktoer(AktoerType.AKTOER_ID, "1234")
        )
    }

    @Test
    fun `Skal migrere data fra arena mapping til person tabell `() {
        withMigratedDb {
            val numberOfInserts = 100
            val commands = (1..numberOfInserts).toList().map { InntektId(ULID().nextULID()) to StoreInntektCommand(
                inntektparametre = Inntektparametre(aktørId = Random.nextInt().toString(), vedtakId = Random.nextLong().toString(), beregningsdato = LocalDate.now()),
                inntekt = hentInntektListeResponse
            ) }
            fillArenaMappingTable(TestDataSource.instance, commands)
            val arenaMappingMigrator = ArenaMappingMigrator(TestDataSource.instance)
            val rowsMigrated = arenaMappingMigrator.migrate()

            val inntektStore = PostgresInntektStore(TestDataSource.instance)

            val ids = commands.mapNotNull { (_, command) ->
                inntektStore.fetchInntektIdFromPersonMappingTable(
                    inntektparametre = command.inntektparametre
                )
            }

            val fromOld = commands.mapNotNull { (_, command) ->
                inntektStore.getInntektId(
                    inntektparametre = command.inntektparametre
                )
            }

            fromOld shouldContainAll ids

            rowsMigrated shouldBeExactly numberOfInserts
            ids.size shouldBeExactly numberOfInserts
            ids shouldContainAll commands.map { it.first }
        }
    }

    @Test
    fun `Skal migrere data fra arena mapping til person tabell med samme vedtak id og aktør id men forskjellig beregningsdato`() {
        withMigratedDb {
            val numberOfInserts = 4
            val commandsWithSameVedtakId: List<Pair<InntektId, StoreInntektCommand>> = (1..4).toList().map { InntektId(ULID().nextULID()) to StoreInntektCommand(
                inntektparametre = Inntektparametre(aktørId = "1234", vedtakId = "5678", beregningsdato = LocalDate.now().minusDays(Random.nextInt(365).toLong())),
                inntekt = hentInntektListeResponse
            ) }
            fillArenaMappingTable(TestDataSource.instance, commandsWithSameVedtakId)
            val arenaMappingMigrator = ArenaMappingMigrator(TestDataSource.instance)
            val rowsMigrated = arenaMappingMigrator.migrate()

            val inntektStore = PostgresInntektStore(TestDataSource.instance)

            val ids = commandsWithSameVedtakId.mapNotNull { (_, command) ->
                inntektStore.fetchInntektIdFromPersonMappingTable(
                    inntektparametre = command.inntektparametre
                )
            }

            rowsMigrated shouldBeExactly numberOfInserts
            ids.size shouldBeExactly numberOfInserts
            ids shouldContainAll commandsWithSameVedtakId.map { it.first }
        }
    }

    @Test
    fun ` Migrere kan skje flere ganger `() {

        withMigratedDb {
            val numberOfInserts = 100
            val commands = (1..numberOfInserts).toList().map {
                InntektId(ULID().nextULID()) to StoreInntektCommand(
                    inntektparametre = Inntektparametre(
                        aktørId = Random.nextInt().toString(),
                        vedtakId = Random.nextLong().toString(),
                        beregningsdato = LocalDate.now()
                    ),
                    inntekt = hentInntektListeResponse
                )
            }
            fillArenaMappingTable(TestDataSource.instance, commands)
            val arenaMappingMigrator = ArenaMappingMigrator(TestDataSource.instance)
            val rowsMigrated = arenaMappingMigrator.migrate()
            val rowsMigratedSecondTime = arenaMappingMigrator.migrate()

            val inntektStore = PostgresInntektStore(TestDataSource.instance)

            val ids = commands.mapNotNull { (_, command) ->
                inntektStore.fetchInntektIdFromPersonMappingTable(
                    inntektparametre = command.inntektparametre
                )
            }

            rowsMigrated shouldBeExactly numberOfInserts
            rowsMigratedSecondTime shouldBeExactly 0
            ids.size shouldBeExactly numberOfInserts
            ids shouldContainAll commands.map { it.first }
        }
    }

    private fun fillArenaMappingTable(dataSource: javax.sql.DataSource, commands: List<Pair<InntektId, StoreInntektCommand>>) {
        commands.forEach { (id, command) ->
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
