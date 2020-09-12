dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":util"))

    /* java/kotlin dependencies */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${JvmVersions.COROUTINE}")

    /* reflection dependencies */
    implementation("io.github.classgraph:classgraph:${ReflectionVersions.CLASSGRAPH}")

    /* kotlinscript dependencies */
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:${JvmVersions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:${JvmVersions.KOTLIN}")

    /* security dependencies */
    implementation("org.bouncycastle:bcprov-jdk15on:${SecurityVersions.BOUNCYCASTLE}")

    /* jackson dependencies */
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${JacksonVersions.JACKSON}")

    api("io.guthix:jagex-store-5:${ProjectVersions.JS5_STORE}")
}
