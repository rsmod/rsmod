plugins {
    id("base-conventions")
}

dependencies {
    implementation(libs.fastutil)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.type.typeSymbols)
    implementation(projects.engine.utilsBits)
    implementation("com.github.mpkorstanje:simmetrics-core:4.1.1")
}
