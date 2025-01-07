plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.guice)
    implementation(libs.rsprot.api)
    implementation(projects.api.config)
    implementation(projects.api.playerOutput)
    implementation(projects.api.invtx)
    implementation(projects.api.random)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.utils.utilsFormat)
    implementation(projects.engine.annotations)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.objtx)
    implementation(projects.engine.pathfinder)
    implementation(projects.engine.utilsBits)
    implementation(projects.engine.utilsTime)
    integrationImplementation(projects.engine.coroutine)
    testImplementation(projects.api.testing.testFactory)
    testImplementation(projects.api.testing.testParams)
}
