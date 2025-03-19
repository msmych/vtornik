dependencies {
    implementation("io.ktor:ktor-server-core:3.1.0")
    implementation("io.ktor:ktor-server-netty:3.1.0")
    implementation("io.ktor:ktor-server-html-builder:3.1.0")

    implementation(project(":tmdb-client"))

    testImplementation("io.ktor:ktor-server-test-host:3.1.0")
}