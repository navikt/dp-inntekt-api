package no.nav.dagpenger.inntekt.subsumsjonbrukt

import de.huxhorn.sulky.ulid.ULID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.inntekt.Configuration
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.subasumsjonbrukt.KafkaSubsumsjonBruktDataConsumer
import no.nav.dagpenger.plain.producerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import java.util.concurrent.TimeUnit

val LOGGER = KotlinLogging.logger { }
class KafkaSubsumsjonBruktDataConsumerTest {
    private object Kafka {
        val instance by lazy {
            KafkaContainer("5.3.0").apply { this.start() }
        }
    }

    private val adapter = moshiInstance.adapter<Map<String, Any?>>(Map::class.java).lenient()
    @Test
    fun `Should mark inntekt id as used`() {
        runBlocking {
            val storeMock = mockk<InntektStore>(relaxed = false).apply {
                every { this@apply.markerInntektBrukt(any()) } returns 1
            }
            val config = Configuration().run {
                copy(kafka = kafka.copy(brokers = Kafka.instance.bootstrapServers, user = null, password = null))
            }

            KafkaSubsumsjonBruktDataConsumer.apply {
                create(config, storeMock)
                listen()
            }

            val producer = KafkaProducer<String, String>(
                producerConfig(
                    clientId = "test",
                    bootstrapServers = Kafka.instance.bootstrapServers
                ).also {
                    it[ProducerConfig.ACKS_CONFIG] = "all"
                    it[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
                })

            val id = ULID().nextULID()
            val bruktSubsumsjonData = mapOf("faktum" to mapOf("inntektsId" to id))

            val metaData = producer.send(ProducerRecord(config.subsumsjonBruktDataTopic, "test", adapter.toJson(bruktSubsumsjonData)))
                .get(5, TimeUnit.SECONDS)
            LOGGER.info("Producer produced $bruktSubsumsjonData with meta $metaData")

            TimeUnit.MILLISECONDS.sleep(500)

            verify(exactly = 1) {
                storeMock.markerInntektBrukt(InntektId(id))
            }
        }
    }
}