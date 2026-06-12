plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
}

group = "me.eriknikli"

val rheniumVersion: String by project

version = rheniumVersion