version = "194.0.0"

dependencies {
    implementation(project(":util"))
    implementation(libs.nettyAll)
    implementation(libs.kotlinRetry)
    implementation(libs.jacksonKotlin)
    implementation(libs.guthixBytebuf)
    implementation(libs.js5Store)
}
