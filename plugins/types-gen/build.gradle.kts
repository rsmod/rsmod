plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.cache)
    implementation(projects.game.types)
    implementation(projects.plugins.api)
    implementation(libs.clikt)
    implementation(libs.guice)
    implementation(libs.openrs2Cache)
}
