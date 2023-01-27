version = "209.1.0"

plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.nettyTransport)
    api(libs.nettyHandler)
    api(libs.openrs2Crypto)
    api(libs.openrs2Cache)
    api(libs.openrs2Buffer)
    implementation(libs.kotlinCoroutinesCore)
    implementation(projects.game.protocol)
    implementation(projects.plugins.api)
}
