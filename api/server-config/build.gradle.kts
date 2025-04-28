plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.guice)
    implementation(projects.api.parsers.toml)
    implementation(projects.engine.module)
}
