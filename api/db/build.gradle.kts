plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.flyway.core)
    implementation(libs.guice)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.sqlite.jdbc)
    implementation(projects.engine.module)
}
