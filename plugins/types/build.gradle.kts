plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.guice)
    implementation(libs.kotlinPoet)
    implementation(libs.jacksonDatabind)
}
