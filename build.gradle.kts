import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    kotlin("jvm") version Kotlin.version
    id(Spotless.spotless) version Spotless.version
    id(Shadow.shadow) version Shadow.version
}

buildscript {
    repositories {
        mavenCentral()
    }
}

apply {
    plugin(Spotless.spotless)
}

repositories {
    mavenCentral()
    jcenter()
    maven("http://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}

application {
    applicationName = "dp-inntekt-api"
    mainClassName = "no.nav.dagpenger.inntekt.InntektApiKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencyLocking {
    lockAllConfigurations()
}

configurations.all {
    resolutionStrategy.activateDependencyLocking()
    resolutionStrategy.preferProjectModules()
    resolutionStrategy.eachDependency { DependencyResolver.execute(this) }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(Dagpenger.Events)

    implementation(Ktor.server)
    implementation(Ktor.serverNetty)
    implementation(Ktor.auth)
    implementation(Ktor.authJwt)
    implementation(Ktor.micrometerMetrics)
    implementation(Dagpenger.Biblioteker.ktorUtils)
    implementation(Micrometer.prometheusRegistry)

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

    testImplementation(kotlin("test"))
    testImplementation(Ktor.ktorTest)
    testImplementation(Junit5.api)
    testImplementation(Junit5.params)
    testRuntimeOnly(Junit5.engine)
    testRuntimeOnly(Junit5.vintageEngine)
    testImplementation(Wiremock.standalone)
    testImplementation(KoTest.assertions)
    testImplementation(KoTest.runner)
    testImplementation(TestContainers.postgresql)
    testImplementation(TestContainers.kafka)
    testImplementation(Mockk.mockk)
    testImplementation(JsonAssert.jsonassert)
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        target("*.gradle.kts", "buildSrc/**/*.kt*")
        ktlint()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "6.0.1"
}

tasks.named("shadowJar") {
    dependsOn("test")
}

tasks.named("compileKotlin") {
    dependsOn("spotlessKotlinCheck")
}
