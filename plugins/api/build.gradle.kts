plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game.protocol)
    implementation(libs.nettyBuffer)
    implementation(libs.openrs2Crypto)
}
