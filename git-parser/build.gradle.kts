plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")
}