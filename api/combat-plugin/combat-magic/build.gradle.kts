plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.player)
    implementation(projects.api.playerOutput)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeEditors)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
}
