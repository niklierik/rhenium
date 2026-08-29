plugins {
    id("buildsrc.convention.kotlin-jvm")
}


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
    jvmToolchain(25)
}

group = "me.eriknikli"
