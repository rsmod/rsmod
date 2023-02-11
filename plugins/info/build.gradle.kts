plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh") apply true
}

dependencies {
    implementation(libs.nettyBuffer)
}

jmh {
    profilers = listOf("stack")
}
