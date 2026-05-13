plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
    testImplementation(kotlin("test"))

    implementation("com.google.dagger:dagger:2.56.2")
}

group = "me.eriknikli"

val rheniumVersion: String by project

version = rheniumVersion