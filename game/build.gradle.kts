plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.log)
    implementation(projects.toml)
    implementation(projects.game.coroutines)
    implementation(projects.game.events)
    implementation(projects.game.map)
    implementation(projects.game.protocol)
    implementation(libs.clikt)
    implementation(libs.guava)
    implementation(libs.guice)
    implementation(libs.inlineLogger)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.logback)
    implementation(libs.nettyTransport)
    implementation(libs.openrs2Crypto)
}

subprojects {
    group = rootProject.group.toString() + ".game"
}
