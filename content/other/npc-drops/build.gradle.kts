plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(projects.api.dropTable)
    implementation(projects.api.pluginCommons)
    integrationImplementation(projects.api.dropTable)
    integrationImplementation(projects.api.testing.testRandom)
}
