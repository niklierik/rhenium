plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    val daggerVersion = "2.56.2"

    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":common"))
    implementation(project(":parser"))
    implementation(project(":ast"))
    implementation(project(":semantic-analyzer"))
    implementation(project(":transpiler"))
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("org.antlr:antlr4-runtime:4.13.2")
    implementation("io.github.joelromanpr:commandline-ktx:1.0.0")
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "me.eriknikli.app.AppKt"
}


val rheniumVersion: String by project

version = rheniumVersion

group = "me.eriknikli"