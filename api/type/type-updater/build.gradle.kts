plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(libs.openrs2.cache)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeEditors)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.api.cache)
    implementation(projects.engine.annotations)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
}
