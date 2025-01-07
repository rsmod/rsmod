plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.rsprot.api)
    implementation(projects.engine.game)
}
