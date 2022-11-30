plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    testImplementation(libs.kotlinCoroutinesTest)
    implementation(libs.kotlinCoroutinesCore)
}
