plugins {
    java
    signing
    `maven-publish`
    kotlin("jvm") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.palantir.git-version") version "3.1.0"
}

group = "io.github.leonardobat"
val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

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
    apply(plugin = "maven-publish")
    apply(plugin = "com.palantir.git-version")

    project.version = gitVersion()

    dependencies {
        testImplementation(kotlin("test"))
    }

    tasks.test {
        jvmArgs = listOf("--add-opens", "java.base/java.util=ALL-UNNAMED")
        useJUnitPlatform()
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])

                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                pom {
                    name.set("$groupId:$artifactId")
                    description.set(project.description)
                    url.set("https://github.com/Leonardobat/flink-kotlin-extensions")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("leonardobat")
                            name.set("Leonardo Batista")
                            email.set("leonardobazeta@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/Leonardobat/flink-kotlin-extensions.git")
                        developerConnection.set("scm:git:ssh://git@github.com:Leonardobat/flink-kotlin-extensions.git")
                        url.set("https://github.com/Leonardobat/flink-kotlin-extensions")
                    }
                }
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}
