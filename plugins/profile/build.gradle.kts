plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.plugins)
    implementation(projects.plugins.api)
    implementation(projects.plugins.store)
    implementation(libs.guice)
    implementation(libs.inlineLogger)
    implementation(libs.kotlinCoroutinesCore)
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:1.0.9")
}
