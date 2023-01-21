plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.nettyTransport)
    api(libs.nettyCodecCore)
    api(libs.openrs2Crypto)
}
