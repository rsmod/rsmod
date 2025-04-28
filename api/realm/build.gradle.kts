plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.serverConfig)
    implementation(projects.engine.map)
    implementation(projects.engine.plugin)
}
