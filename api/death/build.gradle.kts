plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.areaChecker)
    implementation(projects.api.config)
    implementation(projects.api.dropTable)
    implementation(projects.api.freeze)
    implementation(projects.api.hunt)
    implementation(projects.api.market)
    implementation(projects.api.random)
    implementation(projects.api.registry)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.playerOutput)
    implementation(projects.api.repo)
    implementation(projects.api.route)
    implementation(projects.api.stats.levelmod)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeEditors)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
}
