plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.game)
    api(projects.game.map)
    api(projects.game.pathfinder)
    implementation(projects.game.scripts)
    implementation(projects.log)
    implementation(projects.plugins.api)
    implementation(libs.guice)
    implementation(libs.inlineLogger)
    implementation(libs.junitApi)
    implementation(libs.junitEngine)
    implementation(libs.logback)
    implementation(libs.openrs2Cache)
}
