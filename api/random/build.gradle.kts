plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
    testImplementation(projects.api.testing.testRandom)
}
