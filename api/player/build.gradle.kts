plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(libs.rsprot.api)
    implementation(projects.api.config)
    implementation(projects.api.playerOutput)
    implementation(projects.api.invtx)
    implementation(projects.api.market)
    implementation(projects.api.random)
    implementation(projects.api.repo)
    implementation(projects.api.route)
    implementation(projects.api.stats.levelmod)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.api.utils.utilsFormat)
    implementation(projects.api.utils.utilsSkills)
    implementation(projects.engine.annotations)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.objtx)
    implementation(projects.engine.plugin)
    implementation(projects.engine.routefinder)
    implementation(projects.engine.utilsBits)
    implementation(projects.engine.utilsTime)
    integrationImplementation(projects.api.net)
    integrationImplementation(projects.engine.coroutine)
    testImplementation(projects.api.testing.testFactory)
    testImplementation(projects.api.testing.testParams)
}
