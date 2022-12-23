plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.nettyTransport)
    api(libs.nettyHandler)
    api(libs.openrs2Crypto)
    api(libs.openrs2Cache)
    implementation(project(":protocol"))
}
