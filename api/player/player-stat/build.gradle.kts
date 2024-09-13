plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.rsprot)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    testImplementation(projects.api.testing.testFactory)
    testImplementation(projects.api.testing.testParams)
}
