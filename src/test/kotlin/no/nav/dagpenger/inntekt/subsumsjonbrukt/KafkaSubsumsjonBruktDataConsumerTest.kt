package no.nav.dagpenger.inntekt.subsumsjonbrukt

import de.huxhorn.sulky.ulid.ULID
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.inntekt.Configuration
import no.nav.dagpenger.inntekt.HealthStatus
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.plain.producerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer

private val LOGGER = KotlinLogging.logger { }
class KafkaSubsumsjonBruktDataConsumerTest {
    private object Kafka {
        val instance by lazy {
            KafkaContainer("5.3.1").apply { this.start() }
        }
    }

    private val producer by lazy {
        KafkaProducer<String, String>(
            producerConfig(
                clientId = "test",
                bootstrapServers = Kafka.instance.bootstrapServers
            ).also {
                it[ProducerConfig.ACKS_CONFIG] = "all"
                it[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            })
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

            val consumer = KafkaSubsumsjonBruktDataConsumer(config, storeMock).apply {
                listen()
            }

            val id = ULID().nextULID()
            val bruktSubsumsjonData = mapOf("faktum" to mapOf("inntektsId" to id))

            val metaData = producer.send(ProducerRecord(config.subsumsjonBruktDataTopic, "test", adapter.toJson(bruktSubsumsjonData)))
                .get(5, TimeUnit.SECONDS)
            LOGGER.info("Producer produced $bruktSubsumsjonData with meta $metaData")

            TimeUnit.MILLISECONDS.sleep(500)

            verify(exactly = 1) {
                storeMock.markerInntektBrukt(InntektId(id))
            }

            consumer.status() shouldBe HealthStatus.UP
        }
    }

    @Test
    fun `Cannot mark inntekt id as used if not present in faktum`() {
        runBlocking {
            val storeMock = mockk<InntektStore>(relaxed = false).apply {
                every { this@apply.markerInntektBrukt(any()) } returns 1
            }
            val config = Configuration().run {
                copy(kafka = kafka.copy(brokers = Kafka.instance.bootstrapServers, user = null, password = null))
            }

            val consumer = KafkaSubsumsjonBruktDataConsumer(config, storeMock).apply {
                listen()
            }

            val bruktSubsumsjonData = mapOf("faktum" to mapOf("manueltGrunnlag" to "122212"))

            val metaData = producer.send(ProducerRecord(config.subsumsjonBruktDataTopic, "test", adapter.toJson(bruktSubsumsjonData)))
                .get(5, TimeUnit.SECONDS)
            LOGGER.info("Producer produced $bruktSubsumsjonData with meta $metaData")

            TimeUnit.MILLISECONDS.sleep(500)

            verify(exactly = 0) {
                storeMock.markerInntektBrukt(any())
            }

            consumer.status() shouldBe HealthStatus.UP
        }
    }

    @Test
    fun `Should have grace period on health status when job is no longer active`() {
        runBlocking {
            val storeMock = mockk<InntektStore>(relaxed = false).apply {
                every { this@apply.markerInntektBrukt(any()) } returns 1
            }
            val config = Configuration().run {
                copy(kafka = kafka.copy(brokers = Kafka.instance.bootstrapServers, user = null, password = null))
            }

            val consumer = KafkaSubsumsjonBruktDataConsumer(config, storeMock).apply {
                listen()
            }

            val illegalInntektId = "bla bla bla" // should
            val bruktSubsumsjonData = mapOf("faktum" to mapOf("inntektsId" to illegalInntektId))

            val metaData = producer.send(ProducerRecord(config.subsumsjonBruktDataTopic, "test", adapter.toJson(bruktSubsumsjonData)))
                .get(5, TimeUnit.SECONDS)
            LOGGER.info("Producer produced $bruktSubsumsjonData with meta $metaData")

            TimeUnit.MILLISECONDS.sleep(500)

            consumer.status() shouldBe HealthStatus.UP
        }
    }

    @Test
    fun `Grace period is over`() {
        val graceperiod1 = KafkaSubsumsjonBruktDataConsumer.Grace(from = ZonedDateTime.now(ZoneOffset.UTC).minusHours(1))
        graceperiod1.expired() shouldBe false
        val graceperiod2 = KafkaSubsumsjonBruktDataConsumer.Grace(from = ZonedDateTime.now(ZoneOffset.UTC).minusHours(4))
        graceperiod2.expired() shouldBe true
    }
}
