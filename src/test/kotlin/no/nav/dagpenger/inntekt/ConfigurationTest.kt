package no.nav.dagpenger.inntekt

import org.junit.Test

class ConfigurationTest {

    private fun withProps(props: Map<String, String>, test: () -> Unit) {
        for ((k, v) in props) {
            System.getProperties()[k] = v
        }
        test()
        for ((k, _) in props) {
            System.getProperties().remove(k)
        }
    }

    @Test
    fun `Configuration is loaded based on application profile`() {
        withProps(mapOf("NAIS_CLUSTER_NAME" to "dev-fss")) {
            with(Configuration()) {
                kotlin.test.assertEquals(Profile.DEV, this.application.profile)
            }
        }

        withProps(mapOf("NAIS_CLUSTER_NAME" to "prod-fss")) {
            with(Configuration()) {
                kotlin.test.assertEquals(Profile.PROD, this.application.profile)
            }
        }
    }

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