version = "209.1.0"

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game.protocol)
    implementation(projects.plugins.api)
    implementation(libs.nettyTransport)
    implementation(libs.nettyHandler)
    implementation(libs.openrs2Crypto)
    implementation(libs.openrs2Cache)
    implementation(libs.openrs2Buffer)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.guava)
}
