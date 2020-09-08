subprojects {
    group = "gg.rsmod.plugins"
    version = "186.0-SNAPSHOT"
}

allprojects {
    dependencies {
        implementation(kotlin("stdlib"))
        implementation(project(":game"))
        implementation("org.jetbrains.kotlin:kotlin-script-runtime:${JvmVersions.KOTLIN_VERSION}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${JvmVersions.COROUTINE_VERSION}")
    }
}
