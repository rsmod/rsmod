plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.jackson.databind)
    implementation(libs.guice)
    implementation(libs.jackson.module.kotlin)
}
