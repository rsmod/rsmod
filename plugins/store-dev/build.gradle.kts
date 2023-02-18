plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.plugins)
    implementation(projects.json)
    implementation(libs.guice)
    implementation(libs.jacksonDatabind)
}
