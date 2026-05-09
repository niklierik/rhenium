plugins {
    kotlin("jvm")
}

group = "me.eriknikli"

val rheniumVersion: String by project

version = rheniumVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}