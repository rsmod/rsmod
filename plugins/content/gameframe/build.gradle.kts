plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.scripts)
    implementation(projects.plugins.api)
    implementation(projects.plugins.types)
    implementation(libs.guice)
    implementation(libs.kotlinScriptRuntime)
}
