plugins {
    `java-test-fixtures`
    kotlin("plugin.serialization") version "2.1.10"
}

dependencies {
    implementation("com.github.jasync-sql:jasync-postgresql:2.2.4")
    implementation("io.ktor:ktor-server-html-builder:3.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    testFixturesImplementation("org.testcontainers:postgresql:1.20.0")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testFixturesImplementation("com.github.jasync-sql:jasync-postgresql:2.2.4")
}