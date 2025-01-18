plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.openrs2.buffer)
    implementation(libs.openrs2.cache)
    implementation(projects.api.cache)
    implementation(projects.api.config)
    implementation(projects.api.parsers.toml)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.api.utils.utilsIo)
    implementation(projects.engine.annotations)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
}
