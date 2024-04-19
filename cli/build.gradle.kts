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
    mainClass = "gus.cli.AppKt"
}

tasks {
    val fatJar = register<Jar>("executableJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("executable")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}