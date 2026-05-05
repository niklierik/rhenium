plugins {
    id("java")
    antlr
}

group = "me.eriknikli"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    antlr("org.antlr:antlr4:4.13.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    outputDirectory = File(outputDirectory.toString() + "/me/eriknikli/lang/grammar")
    arguments = arguments + listOf("-visitor", "-long-messages")
}
