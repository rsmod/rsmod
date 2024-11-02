plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(libs.rsprot.api)
    implementation(projects.api.registry)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
}
