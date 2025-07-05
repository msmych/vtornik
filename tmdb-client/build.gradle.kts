plugins {
    `java-test-fixtures`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlinx.serialization.json)

    testFixturesImplementation(project(":slon"))
}