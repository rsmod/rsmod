plugins {
    id("base-conventions")
    id("benchmark-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    testImplementation(libs.jackson.databind)
    jmh(libs.jackson.databind)
    jmh(libs.kotlin.coroutines.core)
}
