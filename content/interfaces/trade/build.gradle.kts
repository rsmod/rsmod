plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.rsprot.api)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.scriptAdvanced)
    integrationImplementation(projects.api.net)
    integrationImplementation(projects.api.player)
    integrationImplementation(projects.api.testing.testCapture)
}
