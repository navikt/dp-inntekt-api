package no.nav.dagpenger.inntekt.rpc

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.Status
import io.grpc.StatusException
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.db.IllegalInntektIdException
import no.nav.dagpenger.inntekt.db.InntektNotFoundException
import no.nav.dagpenger.inntekt.db.InntektStore

internal class InntektGrpcServer(private val port: Int, inntektStore: InntektStore) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val server: Server = ServerBuilder
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

internal class InntektGrpcApi(private val inntektStore: InntektStore) : InntektHenterGrpcKt.InntektHenterCoroutineImplBase() {
    override suspend fun hentInntekt(request: InntektId): InntektV1 {
        try {
            val inntekt = inntektStore.getInntekt(request.id.let { no.nav.dagpenger.inntekt.db.InntektId(it) })
            // @todo: Need to fetch beregningsdato also, the json need to representated as SpesifisertInntekt
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
