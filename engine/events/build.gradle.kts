plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    testImplementation(libs.kotlin.coroutines.test)
}
