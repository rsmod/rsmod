plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.scriptAdvanced)
    // The op audit test loads every script that handles a Lumbridge npc op.
    integrationImplementation(projects.api.player)
    integrationImplementation(projects.content.generic.genericNpcs)
    integrationImplementation(projects.content.other.ironman)
    integrationImplementation(projects.content.skills.fishing)
    integrationImplementation(projects.content.skills.thieving)
}
