plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.game.coroutines)
    api(projects.plugins.typesGenerated)
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.scripts)
    implementation(projects.game.protocol)
    implementation(projects.game.types)
    implementation(projects.cache)
    implementation(projects.json)
    implementation(projects.plugins.info)
    implementation(libs.inlineLogger)
    implementation(libs.kotlinScriptRuntime)
    implementation(libs.logback)
    implementation(libs.nettyBuffer)
    implementation(libs.nettyTransport)
    implementation(libs.openrs2Cache)
    implementation(libs.openrs2Crypto)
    implementation(libs.openrs2Buffer)
}
