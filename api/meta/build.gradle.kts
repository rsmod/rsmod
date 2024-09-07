plugins {
    id("base-conventions")
    id("meta-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    konsistTestImplementation(libs.konsist)
}
