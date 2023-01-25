plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    implementation(libs.guice)
    api(libs.jacksonDatabind)
    implementation(libs.jacksonKotlin)
    implementation(libs.jacksonToml)
}
