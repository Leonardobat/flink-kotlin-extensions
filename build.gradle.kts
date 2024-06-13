plugins {
    java
    signing
    `maven-publish`
    kotlin("jvm") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.palantir.git-version") version "3.1.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

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

allprojects {
    apply(plugin = "com.palantir.git-version")
    group = "io.github.leonardobat.flink-kotlin-extensions"
    val gitVersion: groovy.lang.Closure<String> by extra
    version = gitVersion()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")

    dependencies {
        testImplementation(kotlin("test"))
    }

    tasks.test {
        jvmArgs = listOf("--add-opens", "java.base/java.util=ALL-UNNAMED")
        useJUnitPlatform()
    }

    java {
        withSourcesJar()
    }

    val dokkaJavadocJar = tasks.register<Jar>("dokkaJavadocJar") {
        dependsOn(tasks.named("dokkaJavadoc"))
        from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }

    val dokkaHtmlJar = tasks.register<Jar>("dokkaHtmlJar") {
        dependsOn(tasks.dokkaHtml)
        from(tasks.dokkaHtml.flatMap { it.outputDirectory })
        archiveClassifier.set("html-docs")
    }

    tasks.withType<GenerateModuleMetadata>().configureEach {
        dependsOn(dokkaJavadocJar, dokkaHtmlJar)
    }

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])

                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()

                    artifact(dokkaJavadocJar)
                    artifact(dokkaHtmlJar)

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
            // TODO: Add Sonatype publishing when Maven Central Publish API support is available for Gradle
        }

        signing {
            useGpgCmd()
            sign(publishing.publications["mavenJava"])
        }
    }
}

kotlin {
    jvmToolchain(17)
}
