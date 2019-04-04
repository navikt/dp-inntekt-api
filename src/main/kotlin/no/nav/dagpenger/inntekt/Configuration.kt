package no.nav.dagpenger.inntekt

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import java.util.UUID

private val localProperties = ConfigurationMap(
    mapOf(
        "database.host" to "localhost",
        "database.port" to "5432",
        "database.name" to "dp-inntekt-db",
        "database.user" to "postgres",
        "database.password" to "postgres",
        "vault.mountpath" to "postgresql/dev/",
        "application.profile" to "LOCAL",
        "application.httpPort" to "8099",
        "hentinntektliste.url" to "https://localhost/inntektskomponenten-ws/rs/api/v1/hentinntektliste",
        "oidc.sts.issuerurl" to "http://localhost/",
        "srvdp.inntekt.api.username" to "postgres",
        "srvdp.inntekt.api.password" to "postgres",
        "unleash.url" to "http://localhost",
        "host" to "local"
    )
)
private val devProperties = ConfigurationMap(
    mapOf(
        "database.host" to "b27dbvl007.preprod.local",
        "database.port" to "5432",
        "database.name" to "dp-inntekt-db-preprod",
        "vault.mountpath" to "postgresql/preprod-fss/",
        "application.profile" to "DEV",
        "application.httpPort" to "8099"
    )
)
private val prodProperties = ConfigurationMap(
    mapOf(
        "database.host" to "fsspgdb.adeo.no",
        "database.port" to "5432",
        "database.name" to "dp-inntekt-db",
        "vault.mountpath" to "postgresql/prod-fss/",
        "application.profile" to "PROD",
        "application.httpPort" to "8099"
    )
)

data class Configuration(
    val database: Database = Database(),
    val vault: Vault = Vault(),
    val application: Application = Application()

) {
    data class Database(
        val host: String = config()[Key("database.host", stringType)],
        val port: String = config()[Key("database.port", stringType)],
        val name: String = config()[Key("database.name", stringType)],
        val user: String? = config().getOrNull(Key("database.user", stringType)),
        val password: String? = config().getOrNull(Key("database.password", stringType))

    )

    data class Vault(
        val mountPath: String = config()[Key("vault.mountpath", stringType)]
    )

    data class Application(
        val profile: Profile = config()[Key("application.profile", stringType)].let { Profile.valueOf(it) },
        val httpPort: Int = config()[Key("application.httpPort", intType)],
        val username: String = config()[Key("srvdp.inntekt.api.username", stringType)],
        val password: String = config()[Key("srvdp.inntekt.api.password", stringType)],
        val hentinntektListeUrl: String = config()[Key("hentinntektliste.url", stringType)],
        val oicdStsUrl: String = config()[Key("oidc.sts.issuerurl", stringType)],
        val name: String = "dp-inntekt-api",
        val instance: String = config().getOrNull(Key("host", stringType)) ?: name + UUID.randomUUID().toString(),
        val unleashUrl: String = config()[Key("unleash.url", stringType)]

    )
}

enum class Profile {
    LOCAL, DEV, PROD
}

private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
    "dev-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> {
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties
    }
}