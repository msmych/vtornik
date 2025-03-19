plugins {
    kotlin("jvm") version "2.1.10"
}

group = "uk.matvey"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("ch.qos.logback:logback-classic:1.5.17")
        runtimeOnly("io.github.oshai:kotlin-logging:7.0.5")

        testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
        testImplementation("io.mockk:mockk:1.13.17")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testImplementation("org.assertj:assertj-core:3.11.1")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(23))
        }
    }

    kotlin {
        jvmToolchain(23)
    }

    tasks.test {
        useJUnitPlatform()
    }
}
