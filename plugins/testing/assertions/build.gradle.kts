plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.game)
    api(projects.game.events)
    api(projects.game.protocol)
    implementation(projects.game)
    implementation(libs.junitApi)
    implementation(libs.junitEngine)
}
