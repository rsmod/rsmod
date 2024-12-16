plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.rsprot.api)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.api.utils.utilsFormat)
}
