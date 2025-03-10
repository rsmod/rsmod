plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.api.combat.combatMagic)
    implementation(projects.api.combat.combatWeapon)
    implementation(projects.api.utils.utilsVars)
    implementation(projects.engine.game)
}
