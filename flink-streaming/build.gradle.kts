
plugins {
    kotlin("jvm")

}

group = "io.github.leonardobat"
version = "0.1-SNAPSHOT"

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