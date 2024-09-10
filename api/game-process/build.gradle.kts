plugins {
    id("base-conventions")
    id("integration-test-suite")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.rsprot)
    implementation(projects.api.config)
    implementation(projects.api.interactions)
    implementation(projects.api.player)
    implementation(projects.api.random)
    implementation(projects.api.registry)
    implementation(projects.api.repo)
    implementation(projects.api.route)
    implementation(projects.api.utils.utilsMap)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.interact)
    implementation(projects.engine.map)
    implementation(projects.engine.pathfinder)
    integrationImplementation(libs.fastutil)
    integrationImplementation(libs.rsprot)
    integrationImplementation(projects.api.player)
    integrationImplementation(projects.api.random)
    integrationImplementation(projects.api.registry)
    integrationImplementation(projects.api.utils.utilsMap)
    integrationImplementation(projects.engine.coroutine)
}