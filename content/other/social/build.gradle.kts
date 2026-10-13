plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.rsprot.api)
    implementation(projects.api.db)
    implementation(projects.api.dbGateway)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.realm)
    implementation(projects.api.scriptAdvanced)
    implementation(projects.api.serverConfig)
    testImplementation(libs.flyway.core)
    testImplementation(libs.sqlite.jdbc)
    integrationImplementation(projects.api.net)
    integrationImplementation(projects.api.player)
    integrationImplementation(projects.api.testing.testCapture)
}
