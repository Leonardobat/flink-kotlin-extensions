plugins {
    java
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.apache.flink:flink-core:${rootProject.ext["flinkVersion"]}")

    testImplementation("org.apache.flink:flink-test-utils:${rootProject.ext["flinkVersion"]}") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}
