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

    antlr("org.antlr:antlr4:4.13.2")
    implementation("org.antlr:antlr4-runtime:4.13.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    arguments = arguments + listOf(
        "-visitor",
        "-long-messages",
        // This tells the ANTLR tool what package statement to write inside the files
        "-package", "me.eriknikli.rhenium.parser"
    )
    outputDirectory =
        file("${project.layout.buildDirectory.get().asFile}/generated-src/antlr/main/me/eriknikli/rhenium/parser")
}

sourceSets {
    main {
        java {
            srcDir("${project.layout.buildDirectory.get().asFile}/generated-src/antlr/main")
        }
    }
}