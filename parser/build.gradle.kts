plugins {
    id("java")
    antlr
}

group = "me.eriknikli"

val rheniumVersion: String by project

version = rheniumVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    antlr("org.antlr:antlr4:4.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-long-messages")
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/me/eriknikli/rhenium/parser".toString())
}