import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") version Kotlin.version
    id(Spotless.spotless) version Spotless.version
}

val grpcVersion = "1.27.2"

repositories {
    jcenter()
    maven("https://jitpack.io")
}

allprojects {
    group = "no.nav.dagpenger"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = Spotless.spotless)

    dependencies {
        implementation(kotlin("stdlib-jdk8"))

        // ulid
        implementation(Ulid.ulid)

        testImplementation(kotlin("test"))
        testImplementation(Junit5.api)
        testRuntimeOnly(Junit5.engine)
        testImplementation(KoTest.assertions)
        testImplementation(KoTest.runner)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<Wrapper> {
        gradleVersion = "6.3"
    }

    spotless {
        kotlin {
            targetExclude("**/generated/**") // ignore generated gRpc stuff
            ktlint()
        }
        kotlinGradle {
            target("*.gradle.kts", "buildSrc/**/*.kt*")
            ktlint()
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }

    tasks.named("compileKotlin") {
        dependsOn("spotlessApply", "spotlessKotlinCheck")
    }

    tasks.named("jar") {
        dependsOn("test")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        jcenter()
        maven("https://jitpack.io")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))

        testImplementation(kotlin("test"))
        testImplementation(Junit5.api)
        testRuntimeOnly(Junit5.engine)
        testImplementation(Mockk.mockk)
    }
}
