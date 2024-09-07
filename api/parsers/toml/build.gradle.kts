plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.jackson.dataformat.toml)
    implementation(libs.guice)
    implementation(libs.jackson.module.kotlin)
}
