plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":git-loader"))
    implementation(project(":metrics-processor"))
    implementation(libs.kotlinx.cli)
}

application {
    mainClass = "cli.AppKt"
}
