package no.nav.dagpenger.inntekt.subsumsjonbrukt

import java.sql.SQLTransientConnectionException
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.coroutines.CoroutineContext
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
import no.nav.dagpenger.streams.PacketDeserializer
import org.apache.kafka.clients.consumer.CommitFailedException
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.RetriableException

internal class KafkaSubsumsjonBruktDataConsumer(
    private val config: Configuration,
    private val inntektStore: InntektStore
) : CoroutineScope, HealthCheck {

    private val logger = KotlinLogging.logger { }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    val grace by lazy {
        Grace()
    }

    data class Grace(
        val duration: Duration = Duration.ofHours(3),
        val from: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
    ) {
        private val expires = from.plus(duration)
        fun expired() = ZonedDateTime.now(ZoneOffset.UTC).isAfter(expires)
    }

    private val job: Job by lazy {
        Job()
    }

    fun listen() {
        launch {
            val creds = config.kafka.user?.let { u ->
                config.kafka.password?.let { p ->
                    KafkaCredential(username = u, password = p)
                }
            }
            logger.info { "Starting ${config.application.id}" }

            KafkaConsumer<String, Packet>(
                consumerConfig(
                    groupId = config.application.id,
                    bootstrapServerUrl = config.kafka.brokers,
                    credential = creds
                ).also {
                    it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = PacketDeserializer::class.java
                    it[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
                    it[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
                    it[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 10
                }
            ).use { consumer ->
                try {
                    consumer.subscribe(listOf(config.subsumsjonBruktDataTopic))
                    while (job.isActive) {
                        val records = consumer.poll(Duration.ofMillis(100))
                        val ids = records.asSequence()
                            .map { record -> record.value() }
                            .map { packet -> InntektId(packet.getMapValue("faktum")["inntektsId"] as String) }
                            .toList()

                        try {
                            ids.forEach { id ->
                                if (inntektStore.markerInntektBrukt(id) == 1) {
                                    logger.info("Marked inntekt with id $id as used")
                                }
                            }
                            if (ids.isNotEmpty()) {
                                consumer.commitSync()
                            }
                        } catch (e: CommitFailedException) {
                            logger.warn("Kafka threw a commit fail exception, looping back", e)
                        }
                    }
                } catch (e: Exception) {
                    when (e) {
                        is RetriableException,
                        is SQLTransientConnectionException -> {
                            logger.warn("Retriable exception, looping back", e)
                        }
                        else -> {
                            logger.error("Unexpected exception while consuming messages. Stopping", e)
                            stop()
                        }
                    }
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
        logger.info { "Stopping ${config.application.id} consumer" }
        job.cancel()
    }
}
