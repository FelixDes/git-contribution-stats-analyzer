plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(libs.jgit)

}