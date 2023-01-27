plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.log)
    implementation(projects.toml)
    implementation(projects.game.events)
    implementation(libs.clikt)
    implementation(libs.openrs2Cache)
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.guice)
    implementation(libs.logback)
    implementation(libs.inlineLogger)
}
