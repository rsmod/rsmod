plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.playerOutput)
    implementation(projects.api.random)
    implementation(projects.api.script)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.utils.utilsVars)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
    integrationImplementation(projects.api.pluginCommons)
}
