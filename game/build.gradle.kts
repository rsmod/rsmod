plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.clikt)
    api(libs.nettyTransport)
    api(libs.openrs2Buffer)
    api(libs.openrs2Cache)
    implementation(project(":buffer"))
    implementation(project(":config"))
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
}
