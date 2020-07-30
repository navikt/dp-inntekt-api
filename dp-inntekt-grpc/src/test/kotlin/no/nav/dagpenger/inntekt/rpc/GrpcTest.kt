package no.nav.dagpenger.inntekt.rpc

import io.grpc.BindableService
import io.grpc.ManagedChannel
import io.grpc.ServerBuilder
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RunWith(JUnit4::class)
internal abstract class GrpcTest {

    @get:Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private lateinit var executor: ExecutorService

    @Before
    fun setUp() {
        executor = Executors.newFixedThreadPool(10)
    }

    lateinit var channel: ManagedChannel

    @After
    fun tearDown() {
        executor.shutdown()
        if (this::channel.isInitialized) {
            channel.shutdownNow()
        }
    }

    /** Generates a channel to a Greeter server with the specified implementation.
     *  Stolen from https://github.com/grpc/grpc-kotlin/blob/5b6b64a026876b972bf510697786609810c55cac/stub/src/test/java/io/grpc/kotlin/AbstractCallsTest.kt#L152-L171
     *
     * */
    internal fun makeChannel(impl: BindableService): ManagedChannel {
        val serverName = InProcessServerBuilder.generateName()

        grpcCleanup.register(
            InProcessServerBuilder.forName(serverName)
                .run { this as ServerBuilder<*> } // workaround b/123879662
                .executor(executor)
                .addService(impl)
                .build()
                .start()
        )

        return grpcCleanup.register(
            InProcessChannelBuilder
                .forName(serverName)
                .run { this as io.grpc.ManagedChannelBuilder<*> } // workaround b/123879662
                .executor(executor)
                .build()
        )
    }
}
