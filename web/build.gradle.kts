plugins {
    kotlin("plugin.serialization") version "2.1.10"
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.1.1")
    implementation("io.ktor:ktor-server-netty:3.1.1")
    implementation("io.ktor:ktor-server-html-builder:3.1.1")
    implementation("io.ktor:ktor-client-core:3.1.1")
    implementation("io.ktor:ktor-client-cio:3.1.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
    implementation("io.ktor:ktor-client-logging:3.1.1")
    implementation("io.ktor:ktor-server-auth:3.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("org.flywaydb:flyway-core:11.0.1")
    implementation("com.github.jasync-sql:jasync-postgresql:2.2.4")

    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.0.1")
    testImplementation("org.testcontainers:postgresql:1.20.0")
    runtimeOnly("org.postgresql:postgresql:42.7.4")

    implementation(project(":slon"))
    implementation(project(":domain"))
    implementation(project(":tmdb-client"))

    testImplementation("io.ktor:ktor-client-mock:3.1.1")
    testImplementation("io.ktor:ktor-server-test-host:3.1.1")
    testImplementation(testFixtures(project(":slon")))
    testImplementation(testFixtures(project(":domain")))
    testImplementation(testFixtures(project(":tmdb-client")))
}