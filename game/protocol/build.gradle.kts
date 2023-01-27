plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.nettyTransport)
    api(libs.nettyCodecCore)
    api(libs.openrs2Crypto)
}
