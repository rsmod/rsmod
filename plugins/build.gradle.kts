val rootPluginDir = projectDir
val rootPluginBuildDir = buildDir

subprojects {
    group = "org.rsmod.plugins"

    val relative = projectDir.relativeTo(rootPluginDir)
    buildDir = rootPluginBuildDir.resolve(relative)
}

allprojects {
    dependencies {
        implementation(kotlin("stdlib"))
        implementation(project(":game"))
        implementation("org.jetbrains.kotlin:kotlin-script-runtime:${Versions.KOTLIN}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}")
    }
}
