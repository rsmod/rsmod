plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    testImplementation(projects.api.testing.testParams)
}
