plugins {
    id("base-conventions")
}

dependencies {
    implementation(projects.api.pluginCommons)
    testImplementation(projects.api.testing.testParams)
}
