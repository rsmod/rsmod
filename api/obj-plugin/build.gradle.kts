plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.invtx)
    implementation(projects.api.player)
    implementation(projects.api.repo)
    implementation(projects.api.script)
    implementation(projects.api.scriptAdvanced)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.objtx)
    implementation(projects.engine.plugin)
}