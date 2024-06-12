
plugins {
    kotlin("jvm")

}

description = "A collection of helper functions for Apache Flink® streaming API in Kotlin projects."

repositories {
    mavenCentral()
}

dependencies {
    api("org.apache.flink:flink-streaming-java:${rootProject.ext["flinkVersion"]}")
    implementation(project(":flink-core"))

    testImplementation("org.apache.flink:flink-test-utils:${rootProject.ext["flinkVersion"]}") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}