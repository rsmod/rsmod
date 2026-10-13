plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.scriptAdvanced)
    integrationImplementation(projects.api.pluginCommons)
}
