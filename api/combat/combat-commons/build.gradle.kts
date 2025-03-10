plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.api.utils.utilsVars)
    implementation(projects.engine.game)
}
