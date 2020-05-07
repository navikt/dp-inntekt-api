import com.google.protobuf.gradle.* // ktlint-disable no-wildcard-imports

val grpcVersion = "1.29.0"
val grpcKotlinVersion = "0.1.1"
val protbufVersion = "3.11.1"
val protobufGradleVersion = "0.8.12"

plugins {
    kotlin("jvm")
    id("com.google.protobuf") version "0.8.12"
}

apply(plugin = "com.google.protobuf")

// To get intellij to make sense of generated sources
java {
    val mainJavaSourceSet: SourceDirectorySet = sourceSets.getByName("main").java
    val protoSrcDir = "$buildDir/generated/source/proto/main"
    mainJavaSourceSet.srcDirs("$protoSrcDir/java", "$protoSrcDir/grpc", "$protoSrcDir/grpckotlin")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // Grpc and Protobuf
    protobuf(files("src/proto/"))
    implementation("com.google.protobuf:protobuf-gradle-plugin:$protobufGradleVersion")
    api("io.grpc:grpc-api:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    if (JavaVersion.current().isJava9Compatible) {
        // Workaround for @javax.annotation.Generated
        // see: https://github.com/grpc/grpc-java/issues/3633
        compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    }
}

// Could not resolve all files for configuration ':protomodule:compileProtoPath' bug
// https://github.com/google/protobuf-gradle-plugin/issues/391 and
// https://github.com/kotest/kotest/issues/1366
configurations.forEach {
    if (it.name.toLowerCase().contains("proto")) {
        it.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "java-runtime"))
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protbufVersion"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckotlin") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckotlin")
            }
        }
    }
}

fun ProtobufConfigurator.generateProtoTasks(action: ProtobufConfigurator.GenerateProtoTaskCollection.() -> Unit) {
    generateProtoTasks(closureOf(action))
}

sourceSets {
    create("proto") {
        proto {
            srcDir("src/main/proto")
        }
    }
}
