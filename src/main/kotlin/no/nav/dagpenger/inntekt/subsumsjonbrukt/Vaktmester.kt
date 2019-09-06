package no.nav.dagpenger.inntekt.subsumsjonbrukt

import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import javax.sql.DataSource

class Vaktmester(private val dataSource: DataSource, private val lifeSpanInDays: Int = 90) {
    fun rydd() {
        using(sessionOf(dataSource)) { session ->
            session.transaction { transaction ->
                transaction.run(
                    queryOf(
                        """
                            DELETE FROM inntekt_v1 WHERE brukt = false AND timestamp  < (now() - (make_interval(days := :days)))
                        """.trimIndent(), mapOf(
                            "days" to lifeSpanInDays
                        )
                    ).asUpdate
                )
            }
        }
    }
}
