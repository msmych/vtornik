plugins {
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation("org.testcontainers:postgresql:1.20.0")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testFixturesImplementation("com.github.jasync-sql:jasync-postgresql:2.2.4")
}