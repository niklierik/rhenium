plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
}

group = "me.eriknikli"

val rheniumVersion: String by project

version = rheniumVersion

repositories {
    mavenCentral()
}

dependencies {
    val daggerVersion: String by project
    testImplementation(kotlin("test"))
    implementation(project(":ast"))
    implementation(project(":semanticContext"))
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}