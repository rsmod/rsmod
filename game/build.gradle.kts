plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.clikt)
    api(libs.nettyTransport)
    api(libs.openrs2Buffer)
    api(libs.openrs2Cache)
    implementation(projects.buffer)
    implementation(projects.log)
    implementation(projects.toml)
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.guice)
}
