package no.nav.dagpenger.inntekt.db

import com.squareup.moshi.JsonAdapter
import de.huxhorn.sulky.ulid.ULID
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse

import no.nav.dagpenger.inntekt.moshiInstance
import no.nav.dagpenger.inntekt.v1.InntektRequest
import org.postgresql.util.PSQLException
import java.time.LocalDate
import javax.sql.DataSource

class PostgresInntektStore(private val dataSource: DataSource) : InntektStore {

    private val adapter: JsonAdapter<InntektkomponentResponse> = moshiInstance.adapter(InntektkomponentResponse::class.java)
    private val ulidGenerator = ULID()

    override fun getInntektId(request: InntektRequest): InntektId? {
        try {
            return using(sessionOf(dataSource)) { session ->
                session.run(
                        queryOf(
                                """SELECT inntektId
                                    FROM inntekt_V1_arena_mapping
                                    WHERE aktørId = ? AND vedtakid = ? AND beregningsdato = ?
                            """.trimMargin(), request.aktørId, request.vedtakId, request.beregningsDato).map { row ->
                            InntektId(row.string("inntektId"))
                        }.asSingle)
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
            ) ?: throw InntektIdNotFoundException("Inntekt with id $inntektId not found.")
        }
    }

    override fun insertInntekt(request: InntektRequest, inntekt: InntektkomponentResponse): StoredInntekt {
        try {
            val inntektId = InntektId(ulidGenerator.nextULID())
            using(sessionOf(dataSource)) { session ->
                session.transaction { tx ->

                    tx.run(queryOf("INSERT INTO inntekt_V1 VALUES (?, (to_json(?::json)))", inntektId.id, adapter.toJson(inntekt)).asUpdate)
                    tx.run(queryOf("INSERT INTO inntekt_V1_arena_mapping VALUES (?, ?, ?, ?)", inntektId.id, request.aktørId, request.vedtakId, request.beregningsDato).asUpdate)
                }
            }
            return getInntekt(inntektId)
        } catch (p: PSQLException) {
            throw StoreException(p.message!!)
        }
    }

    override fun getInntekt(inntektId: InntektId): StoredInntekt {
        return using(sessionOf(dataSource)) { session ->
            session.run(queryOf(""" SELECT id, inntekt from inntekt_V1 where id = ?""", inntektId.id).map { row -> StoredInntekt(InntektId(row.string("id")), adapter.fromJson(row.string("inntekt"))!!) }
                    .asSingle)
                    ?: throw InntektNotFoundException("Inntekt with id $inntektId not found.")
        }
    }
}
