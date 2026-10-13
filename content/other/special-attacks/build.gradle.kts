plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.specials)
    integrationImplementation(libs.fastutil)
    integrationImplementation(projects.api.hitPlugin)
    integrationImplementation(projects.api.pluginCommons)
    integrationImplementation(projects.api.specials)
}
