dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":util"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLIN}")
    implementation("io.github.classgraph:classgraph:${Versions.CLASSGRAPH}")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:${Versions.KOTLIN}")
    implementation("org.bouncycastle:bcprov-jdk15on:${Versions.BOUNCYCASTLE}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")
    implementation("io.guthix:jagex-store-5:${Versions.JS5_STORE}")
}
