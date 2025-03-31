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
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
    testImplementation(libs.fastutil)
    testImplementation(projects.api.testing.testFactory)
    testImplementation(projects.api.testing.testParams)
}
