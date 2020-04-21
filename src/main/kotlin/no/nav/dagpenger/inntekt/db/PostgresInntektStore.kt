package no.nav.dagpenger.inntekt.db

import com.squareup.moshi.JsonAdapter
import de.huxhorn.sulky.ulid.ULID
import io.prometheus.client.Summary
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.sql.DataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.BehandlingsKey
import no.nav.dagpenger.inntekt.HealthCheck
import no.nav.dagpenger.inntekt.HealthStatus
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.moshiInstance
import org.postgresql.util.PGobject
import org.postgresql.util.PSQLException

internal class PostgresInntektStore(private val dataSource: DataSource) : InntektStore, HealthCheck {
    private val LOGGER = KotlinLogging.logger {}

    companion object {
        private val markerInntektTimer = Summary.build()
            .name("marker_inntekt_brukt")
            .help("Hvor lang tid det tar å markere en inntekt brukt (i sekunder")
            .register()
    }
    override fun getManueltRedigert(inntektId: InntektId): ManueltRedigert? {
        try {
            return using(sessionOf(dataSource)) { session ->
                session.run(
                    queryOf(
                        """SELECT redigert_av
                                    FROM inntekt_V1_manuelt_redigert
                                    WHERE inntekt_id = ?
                            """.trimMargin(), inntektId.id
                    ).map { row ->
                        ManueltRedigert(row.string(1))
                    }.asSingle
                )
            }
        } catch (p: PSQLException) {
            throw StoreException(p.message!!)
        }
    }

    private val adapter: JsonAdapter<InntektkomponentResponse> =
        moshiInstance.adapter(InntektkomponentResponse::class.java)
    private val ulidGenerator = ULID()

    override fun getInntektId(request: BehandlingsKey): InntektId? {
        try {
            return using(sessionOf(dataSource)) { session ->
                session.run(
                    queryOf(
                        """SELECT inntektId
                                    FROM inntekt_V1_arena_mapping
                                    WHERE aktørId = ? AND vedtakid = ? AND beregningsdato = ?
                                    ORDER BY timestamp DESC LIMIT 1
                            """.trimMargin(), request.aktørId, request.vedtakId, request.beregningsDato
                    ).map { row ->
                        InntektId(row.string("inntektId"))
                    }.asSingle
                )
            }
        } catch (p: PSQLException) {
            throw StoreException(p.message!!)
        }
    }

    override fun getBeregningsdato(inntektId: InntektId): LocalDate {
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    """SELECT beregningsdato
                                FROM inntekt_V1_arena_mapping
                                WHERE inntektId = ?
                        """.trimMargin(), inntektId.id
                ).map { row ->
                    row.localDate("beregningsdato")
                }.asSingle
            ) ?: throw InntektNotFoundException("Inntekt with id $inntektId not found.")
        }
    }

    override fun getInntekt(inntektId: InntektId): StoredInntekt {
        return using(sessionOf(dataSource)) { session ->
            session.run(
                queryOf(
                    """ SELECT id, inntekt, manuelt_redigert, timestamp from inntekt_V1 where id = ?""",
                    inntektId.id
                ).map { row ->
                    StoredInntekt(
                        inntektId = InntektId(row.string("id")),
                        inntekt = adapter.fromJson(row.string("inntekt"))!!,
                        manueltRedigert = row.boolean("manuelt_redigert"),
                        timestamp = row.zonedDateTime("timestamp").toLocalDateTime()
                    )
                }
                    .asSingle)
                ?: throw InntektNotFoundException("Inntekt with id $inntektId not found.")
        }
    }

    override fun insertInntekt(
        request: BehandlingsKey,
        inntekt: InntektkomponentResponse,
        manueltRedigert: ManueltRedigert?,
        created: ZonedDateTime
    ): StoredInntekt {
        try {

            val inntektId = InntektId(ulidGenerator.nextULID())
            using(sessionOf(dataSource)) { session ->
                session.transaction { tx ->
                    tx.run(
                        queryOf(
                            "INSERT INTO inntekt_V1 (id, inntekt, manuelt_redigert, timestamp) VALUES (:id, :data, :manuelt, :created)", mapOf(
                                "id" to inntektId.id,
                                "created" to created,
                                "data" to PGobject().apply {
                                    type = "jsonb"
                                    value = adapter.toJson(inntekt)
                                },
                                when (manueltRedigert) {
                                    null -> "manuelt" to false
                                    else -> "manuelt" to true
                                }
                            )

                        ).asUpdate
                    )
                    tx.run(
                        queryOf(
                            "INSERT INTO inntekt_V1_arena_mapping VALUES (:id, :aktor, :vedtak, :beregningsdato)", mapOf(
                                "id" to inntektId.id,
                                "aktor" to request.aktørId,
                                "vedtak" to request.vedtakId,
                                "beregningsdato" to request.beregningsDato
                            )
                        ).asUpdate
                    )

                    manueltRedigert?.let {
                        tx.run(
                            queryOf(
                                "INSERT INTO inntekt_V1_manuelt_redigert VALUES(:id,:redigert)", mapOf(
                                    "id" to inntektId.id,
                                    "redigert" to it.redigertAv
                                )
                            ).asUpdate
                        )
                    }
                }
            }
            return getInntekt(inntektId)
        } catch (p: PSQLException) {
            throw StoreException(p.message!!)
        }
    }

    override fun markerInntektBrukt(inntektId: InntektId): Int {
        val timer = markerInntektTimer.startTimer()
        try {
            return using(sessionOf(dataSource)) { session ->
                session.transaction { tx ->
                    tx.run(
                        queryOf(
                            """
                                UPDATE inntekt_V1 SET brukt = true WHERE id = :id;
                            """.trimIndent(),
                            mapOf("id" to inntektId.id)
                        ).asUpdate
                    )
                }
            }
        } catch (p: PSQLException) {
            throw StoreException(p.message!!)
        } finally {
            timer.observeDuration()
        }
    }

    override fun status(): HealthStatus {
        return try {
            using(sessionOf(dataSource)) { session -> session.run(queryOf(""" SELECT 1""").asExecute) }.let { HealthStatus.UP }
        } catch (p: PSQLException) {
            LOGGER.error("Failed health check", p)
            return HealthStatus.DOWN
        }
    }
}
