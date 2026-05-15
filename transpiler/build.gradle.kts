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
    val daggerVersion = "2.56.2"
    implementation(project(":ast"))
    implementation(project(":semanticContext"))
    implementation("com.google.dagger:dagger:$daggerVersion")
    testImplementation(kotlin("test"))
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}