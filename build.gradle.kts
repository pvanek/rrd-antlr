plugins {
    kotlin("jvm") version "1.9.21"
    java
    antlr
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cz.yarpen"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
    antlr("org.antlr:antlr4:4.13.0") {
        exclude("org.antlr", "antlr")
    }

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileKotlin {
    dependsOn(tasks["generateGrammarSource"])
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.compileTestKotlin {
    dependsOn(tasks["generateTestGrammarSource"])
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.generateGrammarSource {
    arguments.add("-Dlanguage=Java")
    arguments.add("-visitor")
    arguments.add("-no-listener")
    arguments.add("-package")
    arguments.add("com.cjsoftware.antlr4docgen.parser")
    arguments.add("-lib")
    arguments.add("src/libantlr/")
}

tasks.shadowJar {
    dependsOn("distTar", "distZip")
    archiveBaseName = project.name
    archiveClassifier = ""
    archiveAppendix = ""
}
