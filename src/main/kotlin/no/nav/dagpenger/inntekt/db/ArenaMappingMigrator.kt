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
    }

    @Language("sql")
    private val statement: String = """
                  INSERT INTO inntekt_v1_person_mapping (inntektid, aktørid, vedtakid, beregningsdato)
                  SELECT inntektid, aktørid, vedtakid::text, beregningsdato
                  FROM inntekt_v1_arena_mapping 
                  WHERE inntektid NOT IN (SELECT inntektid from inntekt_v1_person_mapping) ON CONFLICT DO NOTHING 
                """.trimIndent()

    fun migrate(): Int {

        val rowsMigrated = using(sessionOf(datasource)) { session ->
            session.transaction {
                it.run(queryOf(statement).asUpdate)
            }
        }

        logger.info { "Migrated $rowsMigrated rows from inntekt_v1_arena_mapping to inntekt_v1_person_mapping" }

        return rowsMigrated
    }
}
