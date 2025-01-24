plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.cache)
    implementation(projects.api.config)
    implementation(projects.api.gameProcess)
    implementation(projects.api.player)
    implementation(projects.api.playerOutput)
    implementation(projects.api.script)
    implementation(projects.api.scriptAdvanced)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
    implementation(projects.engine.objtx)
    implementation(projects.engine.plugin)
}
