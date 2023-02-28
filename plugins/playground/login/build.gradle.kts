plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.scripts)
    implementation(projects.plugins.types)
    implementation(projects.plugins.api)
    implementation(projects.plugins.content.gameframe)
    implementation(libs.guice)
    implementation(libs.kotlinScriptRuntime)
}
