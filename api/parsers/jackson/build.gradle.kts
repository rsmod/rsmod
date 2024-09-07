plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
}
