// The code in this file is a convention plugin - a Gradle mechanism for sharing reusable build logic.
// `buildSrc` is a Gradle-recognized directory and every plugin there will be easily available in the rest of the build.
package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin in JVM projects.
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
}

kotlin {
    // Use a specific Java version to make it easier to work in different environments.
    jvmToolchain(25)
}

dependencies {
    val daggerVersion: String by project
    val arrowCoreVersion: String by project
    val slf4jVersion: String by project
    val antlrVersion: String by project

    implementation("io.arrow-kt:arrow-core:$arrowCoreVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.antlr:antlr4-runtime:$antlrVersion")

    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
}

tasks.withType<Test>().configureEach {
    // Configure all test Gradle tasks to use JUnitPlatform.
    useJUnitPlatform()

    // Log information about all test results, not only the failed ones.
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}
