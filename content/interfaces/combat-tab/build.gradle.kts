plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.api.combat.combatSpells)
    implementation(projects.api.combat.combatWeapon)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.scriptAdvanced)
    implementation(projects.api.specials)
    implementation(projects.engine.utilsBits)
}
