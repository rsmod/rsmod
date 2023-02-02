version = "209.1.0"

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.cache)
    implementation(projects.game.protocol)
    implementation(projects.game.types)
    implementation(projects.json)
    implementation(projects.plugins.typesGenerated)
    implementation(libs.nettyBuffer)
    implementation(libs.nettyTransport)
    implementation(libs.openrs2Cache)
    implementation(libs.openrs2Crypto)
    implementation(libs.openrs2Buffer)
}
