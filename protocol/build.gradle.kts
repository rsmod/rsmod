plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.nettyTransport)
    api(libs.nettyCodecCore)
    implementation(project(":crypto"))
}
