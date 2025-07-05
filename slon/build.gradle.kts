plugins {
    `java-test-fixtures`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.jasync.postgresql)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.kotlinx.serialization.json)

    testFixturesImplementation(libs.testcontainers.postgresql)
    testFixturesImplementation(libs.junit.jupiter)
    testFixturesImplementation(libs.jasync.postgresql)
}