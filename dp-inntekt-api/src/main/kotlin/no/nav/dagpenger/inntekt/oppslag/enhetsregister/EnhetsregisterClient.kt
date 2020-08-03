package no.nav.dagpenger.inntekt.oppslag.enhetsregister

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

class EnhetsregisterClient(
    private val baseUrl: String,
    private val httpClient: HttpClient
) {

    suspend fun hentEnhet(orgnummer: String): String {
        return withContext(Dispatchers.IO) {
            try {
                httpClient.get<String>("$baseUrl/api/enheter/$orgnummer")
            } catch (e: ClientRequestException) {
                when (e.response.status.value) {
                    404 -> httpClient.get<String>("$baseUrl/api/underenheter/$orgnummer")
                    else -> throw e
                }
            }
        }
    }
}

@KtorExperimentalAPI
internal fun httpClient(
    engine: HttpClientEngine = CIO.create { requestTimeout = Long.MAX_VALUE }
): HttpClient {
    return HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = Duration.ofSeconds(5).toMillis()
            requestTimeoutMillis = Duration.ofSeconds(15).toMillis()
            socketTimeoutMillis = Duration.ofSeconds(15).toMillis()
        }

        install(Logging) {
            level = LogLevel.INFO
        }
    }
}
