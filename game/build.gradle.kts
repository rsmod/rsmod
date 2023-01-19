plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.clikt)
    api(libs.nettyTransport)
    implementation(project(":buffer"))
    implementation(libs.guava)
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.guice)
}
