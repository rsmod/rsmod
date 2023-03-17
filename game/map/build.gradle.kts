plugins {
    kotlin("jvm")
    id("me.champeau.jmh") apply true
}

dependencies {
    implementation(libs.fastutil)
}
