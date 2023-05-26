plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.plugins.api)
    implementation(projects.plugins.testing)
    implementation(libs.inlineLogger)
    implementation(libs.junitApi)
    implementation(libs.junitEngine)
    implementation(libs.logback)
}

tasks.test {
    systemProperty("junit.jupiter.extensions.autodetection.enabled", false)
}
