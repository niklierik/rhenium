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
    implementation(project(":parser"))
    implementation("com.google.dagger:dagger:2.56.2")
    implementation("org.antlr:antlr4-runtime:4.13.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}