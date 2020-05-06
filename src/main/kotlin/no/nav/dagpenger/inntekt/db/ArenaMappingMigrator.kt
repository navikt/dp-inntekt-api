package no.nav.dagpenger.inntekt.db

import javax.sql.DataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

internal class ArenaMappingMigrator(private val datasource: DataSource) {

    companion object {
        private val logger = KotlinLogging.logger {}
        private val lockKey = 1337
    }

    @Language("sql")
    private val statement: String = """
                  INSERT INTO inntekt_v1_person_mapping (inntektid, aktørid, vedtakid, beregningsdato)
                  SELECT inntektid, aktørid, vedtakid, beregningsdato
                  FROM inntekt_v1_arena_mapping 
                  WHERE inntektid NOT IN (SELECT inntektid from inntekt_v1_person_mapping)
                """.trimIndent()

    fun migrate(): Int {
        try {
            val locked = lock()
            return if (locked) {
                logger.info { "Obtained lock for migrator" }
                val rowsMigrated = using(sessionOf(datasource)) { session ->
                    session.transaction {
                        it.run(queryOf(statement).asUpdate)
                    }
                }

                logger.info { "Migrated $rowsMigrated rows from inntekt_v1_arena_mapping to inntekt_v1_person_mapping" }
                rowsMigrated
            } else {
                logger.info { "Could not obtain lock for migrator" }
                0
            }
        } finally {
            val unlocked = unlock()
            logger.info { "Unlocked = $unlocked for migrator" }
        }
    }

    private fun unlock(): Boolean {
        return using(sessionOf(datasource)) { session ->
            session.run(
                queryOf("select pg_advisory_unlock($lockKey)").map {
                    it.string(1) == "t"
                }.asSingle
            ) ?: false
        }
    }

    private fun lock(): Boolean {
        return using(sessionOf(datasource)) { session ->
            session.run(
                queryOf("select pg_try_advisory_lock($lockKey)").map {
                    it.string(1) == "t"
                }.asSingle
            ) ?: false
        }
    }
}
