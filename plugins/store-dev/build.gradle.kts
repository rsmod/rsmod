plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.json)
    implementation(libs.guice)
    implementation(libs.jacksonDatabind)
}
