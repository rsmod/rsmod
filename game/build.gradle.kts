plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.log)
    implementation(projects.toml)
    implementation(projects.game.coroutines)
    implementation(projects.game.events)
    implementation(projects.game.protocol)
    implementation(libs.clikt)
    implementation(libs.openrs2Crypto)
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.nettyTransport)
    implementation(libs.guice)
    implementation(libs.logback)
    implementation(libs.inlineLogger)
}

subprojects {
    group = rootProject.group.toString() + ".game"
}
