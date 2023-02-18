plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.plugins)
    implementation(projects.game.types)
    implementation(projects.plugins.api)
    implementation(libs.kotlinScriptRuntime)
}
