package no.nav.dagpenger.inntekt

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.dagpenger.streams.KafkaCredential

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
        "oppslag.url" to "https://localhost:8090/api",
        "oidc.sts.issuerurl" to "http://localhost/",
        "jwks.url" to "https://localhost",
        "jwks.issuer" to "https://localhost",
        "srvdp.inntekt.api.username" to "postgres",
        "srvdp.inntekt.api.password" to "postgres",
        "flyway.locations" to "db/migration,db/testdata",
        "api.secret" to "secret",
        "api.keys" to "dp-datalaster-inntekt",
        "kafka.subsumsjon.brukt.data.topic" to "privat-dagpenger-subsumsjon-brukt-data",
        "kafka.bootstrap.servers" to "localhost:9092",
        "vaktmester.aktiv" to true.toString()

    )
)
private val devProperties = ConfigurationMap(
    mapOf(
        "database.host" to "b27dbvl007.preprod.local",
        "database.port" to "5432",
        "database.name" to "dp-inntekt-db-preprod",
        "flyway.locations" to "db/migration,db/testdata",
        "vault.mountpath" to "postgresql/preprod-fss/",
        "hentinntektliste.url" to "https://app-q4.adeo.no/inntektskomponenten-ws/rs/api/v1/hentinntektliste",
        "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api",
        "oppslag.url" to "http://dagpenger-oppslag.default.svc.nais.local/api",
        "oidc.sts.issuerurl" to "https://security-token-service.nais.preprod.local/",
        "jwks.url" to "https://isso-q.adeo.no:443/isso/oauth2/connect/jwk_uri",
        "jwks.issuer" to "https://isso-q.adeo.no:443/isso/oauth2",
        "application.profile" to "DEV",
        "application.httpPort" to "8099",
        "kafka.subsumsjon.brukt.data.topic" to "privat-dagpenger-subsumsjon-brukt-data",
        "kafka.bootstrap.servers" to "b27apvl00045.preprod.local:8443,b27apvl00046.preprod.local:8443,b27apvl00047.preprod.local:8443",
        "vaktmester.aktiv" to true.toString()
    )
)
private val prodProperties = ConfigurationMap(
    mapOf(
        "database.host" to "fsspgdb.adeo.no",
        "database.port" to "5432",
        "database.name" to "dp-inntekt-db",
        "flyway.locations" to "db/migration",
        "vault.mountpath" to "postgresql/prod-fss/",
        "hentinntektliste.url" to "https://app.adeo.no/inntektskomponenten-ws/rs/api/v1/hentinntektliste",
        "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api",
        "oppslag.url" to "http://dagpenger-oppslag.default.svc.nais.local/api",
        "oidc.sts.issuerurl" to "https://security-token-service.nais.adeo.no/",
        "jwks.url" to "https://isso.adeo.no:443/isso/oauth2/connect/jwk_uri",
        "jwks.issuer" to "https://isso.adeo.no:443/isso/oauth2",
        "application.profile" to "PROD",
        "application.httpPort" to "8099",
        "kafka.subsumsjon.brukt.data.topic" to "privat-dagpenger-subsumsjon-brukt-data",
        "kafka.bootstrap.servers" to "a01apvl00145.adeo.no:8443,a01apvl00146.adeo.no:8443,a01apvl00147.adeo.no:8443,a01apvl00148.adeo.no:8443,a01apvl00149.adeo.no:8443,a01apvl150.adeo.no:8443",
        "vaktmester.aktiv" to false.toString()
    )
)

data class Configuration(
    val database: Database = Database(),
    val vault: Vault = Vault(),
    val application: Application = Application(),
    val kafka: Kafka = Kafka(),
    val subsumsjonBruktDataTopic: String = config()[Key("kafka.subsumsjon.brukt.data.topic", stringType)],
    val aktivVaktmester: Boolean = config().getOrElse(Key("vaktmester.aktiv", booleanType), false)

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

    data class Kafka(
        val brokers: String = config()[Key("kafka.bootstrap.servers", stringType)],
        val user: String? = config().getOrNull(Key("srvdp.inntekt.api.username", stringType)),
        val password: String? = config().getOrNull(Key("srvdp.inntekt.api.password", stringType))
    ) {
        fun credential(): KafkaCredential? {
            return if (user != null && password != null) {
                KafkaCredential(user, password)
            } else null
        }
    }

    data class Application(
        val profile: Profile = config()[Key("application.profile", stringType)].let { Profile.valueOf(it) },
        val httpPort: Int = config()[Key("application.httpPort", intType)],
        val username: String = config()[Key("srvdp.inntekt.api.username", stringType)],
        val password: String = config()[Key("srvdp.inntekt.api.password", stringType)],
        val hentinntektListeUrl: String = config()[Key("hentinntektliste.url", stringType)],
        val enhetsregisteretUrl: String = config()[Key("enhetsregisteret.url", stringType)],
        val oppslagUrl: String = config()[Key("oppslag.url", stringType)],
        val oicdStsUrl: String = config()[Key("oidc.sts.issuerurl", stringType)],
        val jwksUrl: String = config()[Key("jwks.url", stringType)],
        val jwksIssuer: String = config()[Key("jwks.issuer", stringType)],
        val name: String = "dp-inntekt-api",
        val apiSecret: String = config()[Key("api.secret", stringType)],
        val allowedApiKeys: List<String> = config()[Key("api.keys", stringType)].split(",").toList()
    )
}

enum class Profile {
    LOCAL, DEV, PROD
}

fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
    "dev-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> {
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties
    }
}
