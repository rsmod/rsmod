plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.engine.game)
}
