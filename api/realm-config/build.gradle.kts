plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.guice)
    implementation(projects.api.db)
    implementation(projects.api.parsers.json)
    implementation(projects.api.realm)
    implementation(projects.engine.map)
    implementation(projects.engine.plugin)
    implementation(projects.server.services)
}
