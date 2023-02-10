plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.guice)
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.7.0")
    implementation("io.github.classgraph:classgraph:4.8.154")
}
