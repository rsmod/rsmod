plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.combat.combatCommons)
    implementation(projects.api.combat.combatManager)
    implementation(projects.api.player)
    implementation(projects.api.type.typeEditors)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.plugin)
}
