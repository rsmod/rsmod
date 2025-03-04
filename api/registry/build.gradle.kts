plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(libs.fastutil)
    implementation(libs.rsprot.api)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.routefinder)
    testImplementation(projects.api.testing.testFactory)
    testImplementation(projects.api.testing.testParams)
    testImplementation(projects.engine.routefinder)
}
