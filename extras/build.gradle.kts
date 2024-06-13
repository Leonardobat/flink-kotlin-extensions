import com.google.protobuf.gradle.id

plugins {
    java
    kotlin("jvm")
    id("com.google.protobuf") version "0.9.4"
}

description = "Additional utilities for common third-party-components like Google's Protobuf and Apache Kafka®"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.protobuf:protobuf-kotlin:3.25.3")
    api("org.apache.flink:flink-core:${rootProject.ext["flinkVersion"]}")
    api("org.apache.flink:flink-connector-kafka:${rootProject.ext["flinkKafkaConnectorVersion"]}")

    testImplementation("org.apache.flink:flink-test-utils:${rootProject.ext["flinkVersion"]}") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

sourceSets {
    test {
        java {
            srcDir("build/generated/source/proto/test/java")
        }
        kotlin {
            srcDir("build/generated/source/proto/test/kotlin")
        }
        proto {
            srcDir("src/test/resources/protos")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                id("kotlin") {
                }
            }
        }
    }
}