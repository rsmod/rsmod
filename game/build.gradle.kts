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
    implementation(projects.config)
    implementation(projects.log)
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.guice)
}
