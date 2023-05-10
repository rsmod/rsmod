plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.guice)
    implementation(libs.kotlinScriptCommon)
    implementation("io.github.classgraph:classgraph:4.8.158")
}
