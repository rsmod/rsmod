plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.content.interfaces.gameframe)
    integrationImplementation(projects.api.player)
}
