plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game.map)
    implementation(projects.plugins.types)
    implementation(libs.nettyBuffer)
    implementation(libs.openrs2Buffer)
    implementation(libs.openrs2Cache)
}
