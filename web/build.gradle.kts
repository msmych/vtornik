
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension

plugins {
    application
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.google.cloud.appengine)
}

dependencies {
    implementation(libs.bundles.std.impl)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.java.jwt)
    implementation(libs.flyway.core)
    implementation(libs.jasync.postgresql)
    implementation(libs.google.cloud.secretmanager)

    runtimeOnly(libs.bundles.std.runtime)
    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)

    implementation(project(":slon"))
    implementation(project(":domain"))
    implementation(project(":tmdb-client"))

    testImplementation(libs.bundles.std.test.impl)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.bundles.ktor.test)
    testImplementation(testFixtures(project(":slon")))
    testImplementation(testFixtures(project(":domain")))
    testImplementation(testFixtures(project(":tmdb-client")))

    testRuntimeOnly(libs.bundles.std.test.runtime)
}

application {
    mainClass = "uk.matvey.vtornik.web.AppKt"
}

tasks.shadowJar {
    transform(ServiceFileTransformer::class.java)
}

configure<AppEngineAppYamlExtension> {
    stage {
        setArtifact("build/libs/web-all.jar")
    }
    deploy {
        version = "GCLOUD_CONFIG"
        projectId = "GCLOUD_CONFIG"
    }
}