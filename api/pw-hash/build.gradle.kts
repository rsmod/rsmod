plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.argon2.jvm)
    implementation(libs.guice)
    implementation(projects.engine.module)
}
