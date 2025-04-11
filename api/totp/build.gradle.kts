plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.apache.commons)
    implementation(libs.guice)
    implementation(libs.otp.kotlin)
    implementation(projects.engine.module)
}
