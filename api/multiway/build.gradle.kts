plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.registry)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
}
