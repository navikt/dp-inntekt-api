package no.nav.dagpenger.inntekt

import org.junit.jupiter.api.Test

class ConfigurationTest {

    @Test
    fun `Default configuration is LOCAL `() {
        with(Configuration()) {
            kotlin.test.assertEquals(Profile.LOCAL, this.application.profile)
        }
    }

    @Test
    fun `System properties overrides hard coded properties`() {
        withProps(mapOf("database.host" to "SYSTEM_DB")) {
            with(Configuration()) {
                kotlin.test.assertEquals("SYSTEM_DB", this.database.host)
            }
        }
    }
}
