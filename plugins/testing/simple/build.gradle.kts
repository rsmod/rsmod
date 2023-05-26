plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.plugins.testing.assertions)
    implementation(projects.plugins.api)
    implementation(projects.plugins.testing)
    implementation(libs.inlineLogger)
    implementation(libs.junitApi)
    implementation(libs.junitEngine)
    implementation(libs.logback)
}
