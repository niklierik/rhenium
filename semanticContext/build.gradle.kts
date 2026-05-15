plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
}


val rheniumVersion: String by project

version = rheniumVersion

repositories {
    mavenCentral()
}

dependencies {
    val daggerVersion = "2.56.2"
    testImplementation(kotlin("test"))
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

group = "me.eriknikli"
