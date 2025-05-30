plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(projects.api.cache)
    implementation(projects.api.parsers.toml)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
}
