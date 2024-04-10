rootProject.name = "git-usage-stats"

include(":cli")
include(":api")
include(":git-loader")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}


plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}


