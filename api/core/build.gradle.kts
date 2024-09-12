plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(libs.rsprot)
    implementation(projects.api.cache)
    implementation(projects.api.gameProcess)
    implementation(projects.api.player)
    implementation(projects.api.random)
    implementation(projects.api.registry)
    implementation(projects.api.repo)
    implementation(projects.api.route)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeEditors)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeResolver)
    implementation(projects.api.type.typeUpdater)
    implementation(projects.api.type.typeVerifier)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
    implementation(projects.engine.pathfinder)
}
