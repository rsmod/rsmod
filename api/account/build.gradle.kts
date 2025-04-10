plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(libs.kotlin.coroutines.core)
    implementation(projects.api.db)
    implementation(projects.api.parsers.jackson)
    implementation(projects.api.parsers.json)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
    implementation(projects.server.services)
}
