package no.nav.dagpenger.inntekt.subsumsjonbrukt

import io.prometheus.client.Summary
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.inntekt.Configuration
import no.nav.dagpenger.inntekt.HealthCheck
import no.nav.dagpenger.inntekt.HealthStatus
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.plain.consumerConfig
import no.nav.dagpenger.streams.KafkaCredential
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.RetriableException
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.coroutines.CoroutineContext

private val markerInntektTimer = Summary.build()
    .name("marker_inntekt_brukt")
    .help("Hvor lang tid det tar å markere en inntekt brukt (i sekunder")
    .create()

internal class KafkaSubsumsjonBruktDataConsumer(
    private val config: Configuration,
    private val inntektStore: InntektStore
) : CoroutineScope, HealthCheck {

    private val SERVICE_APP_ID = "dp-inntekt-api-consumer"
    private val logger = KotlinLogging.logger { }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job + handler

    val grace by lazy {
        Grace()
    }

    data class Grace(val duration: Duration = Duration.ofHours(3), val from: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)) {
        private val expires = from.plus(duration)
        fun expired() = ZonedDateTime.now(ZoneOffset.UTC).isAfter(expires)
    }

    private val job: Job by lazy {
        Job()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        logger.error(exception) { "Caught unhandled exception in $SERVICE_APP_ID. Will keep running!" }
    }

    suspend fun listen() {
        launch {
            val creds = config.kafka.user?.let { u ->
                config.kafka.password?.let { p ->
                    KafkaCredential(username = u, password = p)
                }
            }
            logger.info { "Starting $SERVICE_APP_ID" }

            KafkaConsumer<String, String>(
                consumerConfig(
                    groupId = SERVICE_APP_ID,
                    bootstrapServerUrl = config.kafka.brokers,
                    credential = creds
                ).also {
                    it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
                    it[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
                    it[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
                    it[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 10
                    it[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = 20000
                }
            ).use { consumer ->
                try {
                    consumer.subscribe(listOf(config.subsumsjonBruktDataTopic))
                    while (job.isActive) {
                        val records = consumer.poll(Duration.ofMillis(100))
                        records.asSequence()
                            .map { record -> Packet(record.value()) }
                            .map { packet -> InntektId(packet.getMapValue("faktum")["inntektsId"] as String) }
                            .forEach { id ->
                                val timer = markerInntektTimer.startTimer()
                                if (inntektStore.markerInntektBrukt(id) == 1) {
                                    logger.info("Marked inntekt with id $id as used")
                                }
                                timer.observeDuration()
                            }
                        consumer.commitSync()
                    }
                } catch (e: RetriableException) {
                    logger.warn("Kafka threw a retriable exception, looping back", e)
                }
            }
        }
    }

    override fun status(): HealthStatus {
        return if (job.isActive) HealthStatus.UP else {
            return if (grace.expired()) {
                HealthStatus.DOWN
            } else {
                HealthStatus.UP
            }
        }
    }

    fun stop() {
        logger.info { "Stopping $SERVICE_APP_ID consumer" }
        job.cancel()
    }
}
