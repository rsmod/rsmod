plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.api.combatPlugin.combatWeapon)
    implementation(projects.api.combatPlugin.combatMagic)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.scriptAdvanced)
    implementation(projects.api.specials)
    implementation(projects.engine.utilsBits)
}
