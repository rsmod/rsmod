plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    implementation(libs.rsprot.api)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.map)
    implementation(projects.engine.pathfinder)
    implementation(projects.engine.utilsBits)
}
