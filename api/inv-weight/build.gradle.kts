plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.cache)
    implementation(projects.engine.game)
    implementation(projects.engine.plugin)
}
