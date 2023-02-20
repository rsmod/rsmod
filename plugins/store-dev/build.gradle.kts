plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.scripts)
    implementation(projects.json)
    implementation(projects.plugins.store)
    implementation(libs.guice)
    implementation(libs.jacksonDatabind)
}
