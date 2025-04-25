plugins {
    id("base-conventions")
}

dependencies {
    implementation(libs.rsprot.api)
    implementation(projects.api.invWeight)
    implementation(projects.api.pluginCommons)
}
