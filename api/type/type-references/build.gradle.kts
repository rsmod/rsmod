plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.guice)
    implementation(projects.api.cache)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
}
