plugins {
    id("base-conventions")
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.fastutil)
    implementation(libs.guice)
    implementation(libs.netty.buffer)
    implementation(libs.openrs2.cache)
    implementation(libs.rsprot)
    implementation(projects.api.cache)
    implementation(projects.api.core)
    implementation(projects.api.gameProcess)
    implementation(projects.api.interactions)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.registry)
    implementation(projects.engine.annotations)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.pathfinder)
    implementation(projects.engine.plugin)
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
}
