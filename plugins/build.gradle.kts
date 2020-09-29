val rootPluginDir = projectDir
val rootPluginBuildDir = buildDir

subprojects {
    group = "gg.rsmod.plugins"

    val relative = projectDir.relativeTo(rootPluginDir)
    buildDir = rootPluginBuildDir.resolve(relative)
}

allprojects {
    dependencies {
        implementation(kotlin("stdlib"))
        implementation(project(":game"))
        implementation("org.jetbrains.kotlin:kotlin-script-runtime:${JvmVersions.KOTLIN}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${JvmVersions.COROUTINE}")
    }
}
