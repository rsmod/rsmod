plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    testImplementation(libs.kotlin.coroutines.test)
}
