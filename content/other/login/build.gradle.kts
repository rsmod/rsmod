plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.rsprot.api)
    implementation(projects.api.invWeight)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.realm)
}
