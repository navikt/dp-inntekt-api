package no.nav.dagpenger.inntekt.rpc

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.withContext
import no.nav.dagpenger.events.inntekt.v1.Inntekt
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.events.moshiInstance

interface InntektHenter : Closeable {
    suspend fun hentSpesifisertInntekt(inntektId: String): SpesifisertInntekt
    suspend fun hentKlassifisertInntekt(inntektId: String): Inntekt
}

class InntektHenterWrapper private constructor(
    private val channel: ManagedChannel,
    private val client: InntektHenter = InntektHenterClient(channel)
) : InntektHenter by client {

    constructor(
        port: Int = 50051,
        serveraddress: String,
        apiKey: String
    ) : this (
        ManagedChannelBuilder
            .forTarget("$serveraddress:$port")
            .intercept(ApiKeyInterceptor(apiKey))
            .executor(Dispatchers.IO.asExecutor())
            .usePlaintext().build()
    )

    override fun close() {
        client.close()
    }
}

internal class InntektHenterClient constructor(
    private val channel: ManagedChannel
) : InntektHenter {

    companion object {
        private val spesifisertInntektAdapter = moshiInstance.adapter(SpesifisertInntekt::class.java)!!
        private val klassifisertInntektAdapter = moshiInstance.adapter(Inntekt::class.java)!!
    }

    private val client = InntektHenterGrpcKt.InntektHenterCoroutineStub(channel)

    override suspend fun hentSpesifisertInntekt(inntektId: String): SpesifisertInntekt {
        val request = InntektId.newBuilder().setId(inntektId).build()
        return withContext(Dispatchers.IO) {
            client.hentSpesifisertInntektAsJson(request).let { spesifisertInntektAdapter.fromJson(it.json) }
                ?: throw RuntimeException("Could not get inntekt with id $inntektId")
        }
    }

    override suspend fun hentKlassifisertInntekt(inntektId: String): Inntekt {
        val request = InntektId.newBuilder().setId(inntektId).build()
        return withContext(Dispatchers.IO) {
            client.hentKlassifisertInntektAsJson(request).let { klassifisertInntektAdapter.fromJson(it.json) }
                ?: throw RuntimeException("Could not get inntekt with id $inntektId")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

internal class ApiKeyInterceptor(private val apiKey: String) : ClientInterceptor {

    companion object {
        private val API_KEY_HEADER: Metadata.Key<String> =
            Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER)
    }

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {

        return object :
            ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata) {
                headers.put(API_KEY_HEADER, apiKey)
                super.start(responseListener, headers)
            }
        }
    }
}
