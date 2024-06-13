plugins {
    java
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

description = "A collection of helper functions to enhance the usability of Apache Flink® Core API for Kotlin projects."

repositories {
    mavenCentral()
}

dependencies {
    api("org.apache.flink:flink-core:${rootProject.ext["flinkVersion"]}")

    testImplementation("org.apache.flink:flink-test-utils:${rootProject.ext["flinkVersion"]}") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}
