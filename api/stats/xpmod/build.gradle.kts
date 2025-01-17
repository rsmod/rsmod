plugins {
    id("base-conventions")
    id("integration-test-suite")
}

dependencies {
    implementation(libs.guice)
    implementation(projects.api.config)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
}
