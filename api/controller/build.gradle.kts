plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.utilsBits)
    implementation(projects.engine.utilsTime)
}
