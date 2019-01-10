import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    groovy
    kotlin("jvm") version "1.3.11"
    id("com.diffplug.gradle.spotless") version "3.13.0"
    id("com.github.johnrengelman.shadow") version "4.0.3"
}

buildscript {
    repositories {
        mavenCentral()
    }
}

apply {
    plugin("com.diffplug.gradle.spotless")
}

repositories {
    jcenter()
}

application {
    applicationName = "dagpenger-inntekt-api"
    mainClassName = "no.nav.dagpenger.inntekt.Api"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val ktorVersion = "1.1.1"
val kotlinLoggingVersion = "1.4.9"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")


    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("junit:junit:4.12")
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        target("*.gradle.kts", "additionalScripts/*.gradle.kts")
        ktlint()
    }
}

tasks.withType<Test> {
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}
