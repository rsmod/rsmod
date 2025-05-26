plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.route)
    implementation(projects.api.registry)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
}
