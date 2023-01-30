version = "209.1.0"

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game.protocol)
    implementation(projects.json)
    implementation(libs.nettyBuffer)
    implementation(libs.nettyTransport)
    implementation(libs.openrs2Cache)
    implementation(libs.openrs2Crypto)
    implementation(libs.openrs2Buffer)
}
