dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":util"))
    implementation(libs.kotlinCoroutinesCore)
    implementation(libs.classgraph)
    implementation(libs.kotlinScriptCommon)
    implementation(libs.kotlinScriptRuntime)
    implementation(libs.bouncyCastle)
    implementation(libs.jacksonKotlin)
    implementation(libs.js5Store)
    implementation(libs.nettyAll)
}
