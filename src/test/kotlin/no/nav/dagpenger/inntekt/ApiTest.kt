package no.nav.dagpenger.inntekt

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

import kotlin.test.Test
import kotlin.test.assertEquals

class ApiTest {

    @Test fun ` should be able to Http GET inntekt `() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "inntekt/1234")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val inntekt = Gson().fromJson<Inntekt>(response.content, Inntekt::class.java)
            assertEquals(inntekt, Inntekt.sampleInntekt)
            assertEquals("application/json; charset=UTF-8", response.headers["Content-Type"])
        }
    }
}