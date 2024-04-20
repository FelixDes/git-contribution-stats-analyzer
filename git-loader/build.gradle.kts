plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(libs.jgit)
    implementation(libs.kotlin.logging)
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.validation)

    testImplementation(libs.kotest.core)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}