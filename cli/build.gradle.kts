plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":git-loader"))
    implementation(project(":api"))
    implementation(libs.kotlinx.cli)
}

application {
    mainClass = "org.example.app.AppKt"
}
