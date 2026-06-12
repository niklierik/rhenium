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
    implementation(project(":common"))
    implementation(project(":parser"))
    implementation(project(":semanticContext"))
    testImplementation(project(":parser"))
}

tasks.test {
    useJUnitPlatform()
    dependsOn(":parser:generateGrammarSource")
}

kotlin {
    jvmToolchain(21)
}
