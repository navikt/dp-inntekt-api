package no.nav.dagpenger.inntekt.rpc

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.Status
import io.grpc.StatusException
import java.time.LocalDate
import java.time.ZonedDateTime
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.db.IllegalInntektIdException
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.db.Inntektparametre
import no.nav.dagpenger.inntekt.db.ManueltRedigert
import no.nav.dagpenger.inntekt.db.StoreInntektCommand
import no.nav.dagpenger.inntekt.db.StoredInntekt

internal class InntektGrpcServer(private val port: Int, inntektStore: InntektStore) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    val server: Server = ServerBuilder
        .forPort(port)
        .addService(InntektGrpcApi(inntektStore))
        .build()

    fun start() {
        server.start()
        logger.info { "inntekt gRPC server started, listening on $port" }
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.info { "*** shutting down gRPC server since JVM is shutting down" }
                stop()
                logger.info { "*** inntekt gRPC server  shut down" }
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = InntektGrpcServer(port, object : InntektStore {
        override fun getInntekt(inntektId: no.nav.dagpenger.inntekt.db.InntektId): StoredInntekt {
            TODO("not implemented")
        }

        override fun getInntektId(inntektparametre: Inntektparametre): no.nav.dagpenger.inntekt.db.InntektId? {
            TODO("not implemented")
        }

        override fun getBeregningsdato(inntektId: no.nav.dagpenger.inntekt.db.InntektId): LocalDate {
            TODO("not implemented")
        }

        override fun storeInntekt(command: StoreInntektCommand, created: ZonedDateTime): StoredInntekt {
            TODO("not implemented")
        }

        override fun getManueltRedigert(inntektId: no.nav.dagpenger.inntekt.db.InntektId): ManueltRedigert? {
            TODO("not implemented")
        }

        override fun markerInntektBrukt(inntektId: no.nav.dagpenger.inntekt.db.InntektId): Int {
            TODO("not implemented")
        }
    })
    server.start()
    server.blockUntilShutdown()
}

internal class InntektGrpcApi(private val inntektStore: InntektStore) : InntektHenterGrpcKt.InntektHenterCoroutineImplBase() {
    override suspend fun hentInntekt(request: InntektId): InntektV1 {
        try {
            val inntekt = inntektStore.getInntekt(request.id.let { no.nav.dagpenger.inntekt.db.InntektId(it) })
            return InntektV1.newBuilder()
                .setInntektId(inntekt.inntektId.let { InntektId.newBuilder().setId(it.id).build() })
                .setJson("{}").build()
        } catch (e: InntektNotFoundException) {
            throw StatusException(Status.NOT_FOUND.withDescription("Inntekt with id ${request.id} not found"))
        } catch (e: IllegalInntektIdException) {
            throw StatusException(Status.INVALID_ARGUMENT.withDescription("Id  ${request.id} not a legal inntekt id"))
        }
    }
}
