plugins {
    `java-test-fixtures`
    kotlin("plugin.serialization") version "2.1.10"
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.2.0")
    implementation("io.ktor:ktor-client-cio:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.0")
    implementation("io.ktor:ktor-client-logging:3.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    testFixturesImplementation(project(":slon"))
}