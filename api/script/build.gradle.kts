plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.api.cheat)
    implementation(projects.api.controller)
    implementation(projects.api.gameProcess)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.engine.events)
    implementation(projects.engine.plugin)
    implementation(projects.engine.game)
}
