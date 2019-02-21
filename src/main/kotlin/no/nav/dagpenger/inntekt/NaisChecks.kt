package no.nav.dagpenger.inntekt

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

private val collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry

fun Routing.naischecks() {
    get("/isAlive") {
        call.respondText("ALIVE", ContentType.Text.Plain)
    }
    get("/isReady") {
        call.respondText("READY", ContentType.Text.Plain)
    }
    get("/metrics") {
        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }
}
