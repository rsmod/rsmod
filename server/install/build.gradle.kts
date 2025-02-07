plugins {
    id("base-conventions")
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.clikt)
    implementation(libs.guice)
    implementation(libs.okhttp)
    implementation(libs.openrs2.cache)
    implementation(projects.api.cache)
    implementation(projects.api.cacheEnricher)
    implementation(projects.api.core)
    implementation(projects.api.gameProcess)
    implementation(projects.api.invPlugin)
    implementation(projects.api.parsers.jackson)
    implementation(projects.api.parsers.json)
    implementation(projects.api.parsers.toml)
    implementation(projects.api.shops)
    implementation(projects.engine.annotations)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
    implementation(projects.server.logging)
    implementation(projects.server.shared)
}
