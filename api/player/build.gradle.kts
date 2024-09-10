plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.rsprot)
    implementation(projects.api.config)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.pathfinder)
    implementation(projects.engine.utilsBits)
    implementation(projects.engine.utilsTime)
    integrationImplementation(projects.engine.coroutine)
}