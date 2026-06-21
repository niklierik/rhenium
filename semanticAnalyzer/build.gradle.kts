plugins {
    id("buildsrc.convention.kotlin-jvm")
}

group = "me.eriknikli"

val rheniumVersion: String by project

version = rheniumVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ast"))
    implementation(project(":common"))
    implementation(project(":semanticContext"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}