plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.api.combatPlugin.combatMagic)
    implementation(projects.api.combatPlugin.combatWeapon)
    implementation(projects.api.utils.utilsVars)
    implementation(projects.engine.game)
}
