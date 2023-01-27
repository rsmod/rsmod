plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.nettyTransport)
    implementation(libs.nettyCodecCore)
    implementation(libs.openrs2Crypto)
}
