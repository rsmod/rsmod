dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":util"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${JvmVersions.COROUTINE}")
    implementation("io.github.classgraph:classgraph:${ReflectionVersions.CLASSGRAPH}")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:${JvmVersions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:${JvmVersions.KOTLIN}")
    implementation("org.bouncycastle:bcprov-jdk15on:${SecurityVersions.BOUNCYCASTLE}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${JacksonVersions.JACKSON}")
    implementation("io.guthix:jagex-store-5:${ProjectVersions.JS5_STORE}")
}
