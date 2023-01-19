plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.nettyBuffer)
    implementation(libs.guice)
}
