val kotlin_version: String by project
val logback_version: String by project
val jmdns_version: String by project

plugins {
    kotlin("jvm") version "2.2.21"
    id("io.ktor.plugin") version "3.3.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}

java {
    toolchain {
        // Forces both Java and Kotlin to use JDK 24
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

tasks.withType<JavaExec> {
    // Fixes the "Restricted method" warnings for Netty
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.withType<Test> {
    // Ensures tests also have native access
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

group = "com.jbass"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation("org.jmdns:jmdns:$jmdns_version")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
