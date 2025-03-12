plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.script)
    implementation(projects.engine.events)
    implementation(projects.engine.plugin)
    implementation(projects.engine.game)
}
