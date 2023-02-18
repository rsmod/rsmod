plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.plugins)
    implementation(projects.game.protocol)
    implementation(projects.plugins.api)
    implementation(projects.plugins.store)
    implementation(libs.guava)
    implementation(libs.inlineLogger)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.logback)
    implementation(libs.nettyTransport)
    implementation(libs.nettyHandler)
    implementation(libs.openrs2Crypto)
    implementation(libs.openrs2Cache)
    implementation(libs.openrs2Buffer)
}
