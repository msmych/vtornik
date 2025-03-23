plugins {
    `java-test-fixtures`
    kotlin("plugin.serialization") version "2.1.10"
}

dependencies {
    implementation("com.github.jasync-sql:jasync-postgresql:2.2.4")
    implementation("org.flywaydb:flyway-core:11.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation(project(":slon"))

    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.0.1")
    runtimeOnly("org.postgresql:postgresql:42.7.4")

    testImplementation("org.testcontainers:postgresql:1.20.0")

    testImplementation(testFixtures(project(":slon")))

    testFixturesImplementation(project(":slon"))
}