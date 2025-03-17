plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.engine.annotations)
    implementation(projects.engine.coroutine)
    implementation(projects.engine.events)
    implementation(projects.engine.map)
    implementation(projects.engine.routefinder)
    implementation(projects.engine.utilsBits)
    implementation(projects.engine.utilsSorting)
}
