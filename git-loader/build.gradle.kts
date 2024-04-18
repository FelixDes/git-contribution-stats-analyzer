plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(libs.jgit)
    implementation(libs.kotlin.logging)
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
}