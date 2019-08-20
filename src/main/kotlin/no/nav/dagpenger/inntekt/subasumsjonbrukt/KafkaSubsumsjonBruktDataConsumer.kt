package no.nav.dagpenger.inntekt.subasumsjonbrukt

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.inntekt.Configuration
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.plain.consumerConfig
import no.nav.dagpenger.streams.KafkaCredential
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import kotlin.coroutines.CoroutineContext

internal class KafkaSubsumsjonBruktDataConsumer(
    private val config: Configuration,
    private val inntektStore: InntektStore
) : CoroutineScope {

    private val SERVICE_APP_ID = "dp-inntekt-api-consumer"
    private val logger = KotlinLogging.logger { }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job + handler

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
                }
            ).use { consumer ->
                consumer.subscribe(listOf(config.subsumsjonBruktDataTopic))
                while (job.isActive) {
                    val records = consumer.poll(Duration.ofMillis(100))
                    records.asSequence()
                        .map { record -> Packet(record.value()) }
                        .map { packet -> InntektId(packet.getMapValue("faktum")["inntektsId"] as String) }
                        .onEach { id -> logger.info("Mark inntekt with id $id as used") }
                        .forEach { id -> inntektStore.markerInntektBrukt(id) }
                    consumer.commitSync()
                }
            }
        }
    }

    fun stop() {
        logger.info { "Stopping $SERVICE_APP_ID consumer" }
        job.cancel()
    }
}
