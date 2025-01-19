plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    api(projects.api.cache)
    api(projects.api.config)
    api(projects.api.random)
    api(projects.api.repo)
    api(projects.api.route)
    api(projects.api.testing.testAssertions)
    api(projects.api.testing.testCapture)
    api(projects.api.testing.testFactory)
    api(projects.api.testing.testParams)
    api(projects.api.testing.testRandom)
    api(projects.api.type.typeBuilders)
    api(projects.api.type.typeReferences)
    api(projects.api.type.typeScriptDsl)
    api(projects.engine.events)
    api(projects.engine.game)
    api(projects.engine.map)
    api(projects.engine.objtx)
    api(projects.engine.plugin)
    api(projects.engine.routefinder)
    implementation(libs.bundles.logging)
    implementation(libs.clikt)
    implementation(libs.guice)
    implementation(libs.jupiter.api)
    implementation(projects.api.cache)
    implementation(projects.api.gameProcess)
    implementation(projects.api.invPlugin)
    implementation(projects.api.market)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.registry)
    implementation(projects.api.stats.levelmod)
    implementation(projects.api.stats.xpmod)
    implementation(projects.engine.annotations)
    implementation(projects.server.app)
}
