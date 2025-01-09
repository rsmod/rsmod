plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.fastutil)
    implementation(libs.openrs2.buffer)
    implementation(libs.openrs2.cache)
    implementation(projects.api.registry)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.engine.annotations)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
    implementation(projects.engine.routefinder)
}
