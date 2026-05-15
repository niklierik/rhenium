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

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")

    implementation(project(":parser"))
    implementation(project(":semanticContext"))
    testImplementation(project(":parser"))

    implementation("org.antlr:antlr4-runtime:4.13.2")
    testImplementation("org.antlr:antlr4-runtime:4.13.2")

    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
}

tasks.test {
    useJUnitPlatform()
    dependsOn(":parser:generateGrammarSource")
}

kotlin {
    jvmToolchain(21)
}
