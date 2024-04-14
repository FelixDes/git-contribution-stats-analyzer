plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    implementation(project(":api"))
    testImplementation(libs.kotest.core)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}
