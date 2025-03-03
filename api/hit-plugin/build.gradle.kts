plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.api.config)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.script)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
}
