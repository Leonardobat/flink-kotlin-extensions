plugins {
    java
    kotlin("jvm") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "io.github.leonardobat"
version = "0.1-SNAPSHOT"

val flinkVersion = "1.18.1"

ext {
    mapOf(
        "flinkVersion" to "1.18.1",
        "flinkKafkaConnectorVersion" to "3.1.0-1.18",
        "jupiterVersion" to "5.10.2",
        "protobufVersion" to "3.25.1",
    )
        .forEach { (key, value) -> set(key, value) }
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")

    dependencies {
        testImplementation(kotlin("test"))
    }

    tasks.test {
        jvmArgs = listOf("--add-opens", "java.base/java.util=ALL-UNNAMED")
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(17)
}
