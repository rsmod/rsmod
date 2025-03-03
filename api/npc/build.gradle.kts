plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.random)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.annotations)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.routefinder)
    implementation(projects.engine.utilsBits)
}
