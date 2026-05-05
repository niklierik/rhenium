plugins {
    kotlin("jvm")
}

group = "me.eriknikli"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.antlr:antlr4-runtime:4.13.2")
    implementation("tools.jackson.core:jackson-databind:3.0.4")
    implementation("com.fasterxml.jackson.core:jackson-annotations:3.0-rc5")
    implementation("tools.jackson.core:jackson-core:3.0.4")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}