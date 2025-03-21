plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    api(projects.api.combat.combatCommons)
    api(projects.api.combat.combatWeapon)
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.combat.combatManager)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.utils.utilsVars)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
}
