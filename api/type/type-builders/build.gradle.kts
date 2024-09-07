plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.type.typeScriptDsl)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
}
