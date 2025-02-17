plugins {
    id("base-conventions")
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.engine.utilsBits)
}
