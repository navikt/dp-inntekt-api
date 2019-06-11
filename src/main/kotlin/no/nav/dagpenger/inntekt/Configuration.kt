package no.nav.dagpenger.inntekt

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

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
        "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api",
        "oppslag.url" to "https://localhost:8090",
        "oidc.sts.issuerurl" to "http://localhost/",
        "srvdp.inntekt.api.username" to "postgres",
        "srvdp.inntekt.api.password" to "postgres",
        "flyway.locations" to "db/migration,db/testdata",
        "api.secret" to "secret",
        "api.keys" to "dp-datalaster-inntekt"
    )
)
private val devProperties = ConfigurationMap(
    mapOf(
        "database.host" to "b27dbvl007.preprod.local",
        "database.port" to "5432",
        "database.name" to "dp-inntekt-db-preprod",
        "vault.mountpath" to "postgresql/preprod-fss/",
        "hentinntektliste.url" to "https://app-t6.adeo.no/inntektskomponenten-ws/rs/api/v1/hentinntektliste",
        "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api",
        "oppslag.url" to "https://dagpenger-oppslag.nais.preprod.local",
        "oidc.sts.issuerurl" to "https://security-token-service-t4.nais.preprod.local/",
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
        "hentinntektliste.url" to "https://app.adeo.no/inntektskomponenten-ws/rs/api/v1/hentinntektliste",
        "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api",
        "oppslag.url" to "http://dagpenger-oppslag.default",
        "oidc.sts.issuerurl" to "https://security-token-service.nais.adeo.no/",
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
        val password: String? = config().getOrNull(Key("database.password", stringType)),
        val flywayLocations: List<String> = config().getOrNull(Key("flyway.locations", stringType))?.split(",")
            ?: listOf("db/migration")

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
        val enhetsregisteretUrl: String = config()[Key("enhetsregisteret.url", stringType)],
        val oppslagUrl: String = config()[Key("oppslag.url", stringType)],
        val oicdStsUrl: String = config()[Key("oidc.sts.issuerurl", stringType)],
        val name: String = "dp-inntekt-api",
        val apiSecret: String = config()[Key("api.secret", stringType)],
        val allowedApiKeys: List<String> = config()[Key("api.keys", stringType)].split(",").toList()
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