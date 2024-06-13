plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "flink-kotlin-extensions"
include("core")
include("streaming")
include("extras")
