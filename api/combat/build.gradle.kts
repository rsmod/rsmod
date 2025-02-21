plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.player)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.api.type.typeReferences)
}
