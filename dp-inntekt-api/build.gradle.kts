plugins {
    application
    id(Shadow.shadow) version Shadow.version
    id(Graphql.graphql) version Graphql.version
}

buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    maven("https://packages.confluent.io/maven/")
}

application {
    applicationName = "dp-inntekt-api"
    mainClassName = "no.nav.dagpenger.inntekt.ApplicationKt"
}

val grpcVersion = "1.29.0"

dependencies {
    implementation(project(":dp-inntekt-grpc"))

    implementation(Dagpenger.Events)

    implementation(Ktor.server)
    implementation(Ktor.serverNetty)
    implementation(Ktor.auth)
    implementation(Ktor.authJwt)
    implementation(Ktor.micrometerMetrics)
    implementation(Dagpenger.Biblioteker.ktorUtils)
    implementation(Micrometer.prometheusRegistry)

    implementation(Graphql.client)

    implementation(Moshi.moshi)
    implementation(Moshi.moshiAdapters)
    implementation(Moshi.moshiKotlin)
    implementation(Moshi.moshiKtor)

    implementation(Dagpenger.Streams)
    implementation(Kafka.clients)

    implementation(Kotlin.Logging.kotlinLogging)

    implementation(Fuel.fuel)
    implementation(Fuel.fuelMoshi)
    implementation(Fuel.library("coroutines"))

    implementation(Log4j2.api)
    implementation(Log4j2.core)
    implementation(Log4j2.slf4j)
    implementation(Log4j2.library("jul")) // The Apache Log4j implementation of java.util.logging. java.util.logging used by gprc
    implementation(Log4j2.Logstash.logstashLayout)

    implementation(Ulid.ulid)

    implementation(Dagpenger.Biblioteker.stsKlient)

    implementation(Database.Flyway)
    implementation(Database.HikariCP)
    implementation(Database.Postgres)
    implementation(Database.Kotlinquery)
    implementation(Konfig.konfig)
    implementation(Database.VaultJdbc) {
        exclude(module = "slf4j-simple")
        exclude(module = "slf4j-api")
    }

    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.log4j2)

    implementation(Bekk.nocommons)

    implementation(Kotlinx.bimap)

    // grpc
    testImplementation("io.grpc:grpc-testing:$grpcVersion")
    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")

    testImplementation(kotlin("test"))
    testImplementation(Ktor.ktorTest)
    testImplementation(Junit5.api)
    testImplementation(Junit5.params)
    testRuntimeOnly(Junit5.engine)
    testRuntimeOnly(Junit5.vintageEngine)
    testImplementation(Wiremock.standalone)
    testImplementation(KoTest.assertions)
    testImplementation(KoTest.runner)
    testImplementation(KoTest.property)
    testImplementation(TestContainers.postgresql)
    testImplementation(TestContainers.kafka)
    testImplementation(Mockk.mockk)
    testImplementation(JsonAssert.jsonassert)
}

graphql {
    client {
        sdlEndpoint = "https://navikt.github.io/pdl/pdl-api-sdl.graphqls"
        packageName = "no.nav.pdl"
    }
}

tasks.named("shadowJar") {
    dependsOn("test")
}

tasks.named("jar") {
    dependsOn("test")
}

tasks.named("compileKotlin") {
    dependsOn("spotlessCheck")
    dependsOn("graphqlGenerateClient")
}

tasks.named("test") {
    dependsOn("copySchemaToResources")
}

tasks.register<Copy>("copySchemaToResources") {
    dependsOn("graphqlDownloadSDL")

    from(buildDir) {
        include("schema.graphql")
    }
    into("src/main/resources")
}

dependencyLocking {
    lockAllConfigurations()
}

configurations.all {
    resolutionStrategy.activateDependencyLocking()
    resolutionStrategy.preferProjectModules()
    resolutionStrategy.eachDependency { DependencyResolver.execute(this) }
}
