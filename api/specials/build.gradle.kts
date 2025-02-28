plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    api(projects.api.combatPlugin.combatCommons)
    api(projects.api.combatPlugin.combatWeapon)

    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.player)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.utils.utilsVars)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
}
