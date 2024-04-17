plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(libs.jgit)
}