plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.9.0"
    id("io.ktor.plugin") version "2.3.7"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("ApplicationKt")
}
val serializationVersion = "1.6.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")

    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

    implementation("at.favre.lib:bcrypt:0.9.0")

    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:2.3.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    implementation("io.ktor:ktor-server-call-logging:2.3.4")
    implementation("io.ktor:ktor-server-cors:2.3.4")
    implementation("io.ktor:ktor-server-auth:$2.3.4")
    implementation("io.ktor:ktor-server-auth-jwt:$2.3.4")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}
