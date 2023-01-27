plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.openrs2Cache)
    implementation(projects.buffer)
    implementation(projects.log)
    implementation(projects.toml)
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.guice)
    implementation(libs.logback)
    implementation(libs.inlineLogger)
}
