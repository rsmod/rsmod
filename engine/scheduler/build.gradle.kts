plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    testImplementation(libs.kotlin.coroutines.test)
}
