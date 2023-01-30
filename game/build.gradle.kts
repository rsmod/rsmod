plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.cache)
    implementation(projects.log)
    implementation(projects.toml)
    implementation(projects.game.events)
    implementation(projects.game.protocol)
    implementation(libs.clikt)
    implementation(libs.openrs2Cache)
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.nettyTransport)
    implementation(libs.guice)
    implementation(libs.logback)
    implementation(libs.inlineLogger)
}
