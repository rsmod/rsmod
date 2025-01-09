plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(projects.api.pluginCommons)
    integrationImplementation(projects.api.config)
    integrationImplementation(projects.api.player)
    integrationImplementation(projects.api.type.typeReferences)
}
