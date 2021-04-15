val rootPluginDir = projectDir
val rootPluginBuildDir = buildDir

val appDir = project(":all").projectDir
val pluginConfigDir = appDir.resolve("plugins").resolve("resources")

val libsAlias = libs

subprojects {
    val relative = projectDir.relativeTo(rootPluginDir)
    buildDir = rootPluginBuildDir.resolve(relative)
    group = "org.rsmod.plugins"

    sourceSets {
        main {
            output.setResourcesDir(pluginConfigDir.resolve(relative))
        }
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(project(":game"))
        implementation(libsAlias.kotlinScriptRuntime)
        implementation(libsAlias.kotlinCoroutinesCore)
        implementation(libsAlias.rsmodPathfinder)
    }
}
