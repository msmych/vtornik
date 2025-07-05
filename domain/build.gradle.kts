plugins {
    `java-test-fixtures`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.jasync.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":slon"))

    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.bundles.std.test.impl)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(testFixtures(project(":slon")))

    testRuntimeOnly(libs.bundles.std.test.runtime)

    testFixturesImplementation(project(":slon"))
}