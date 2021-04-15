val rootPluginDir = projectDir
val rootPluginBuildDir = buildDir

val appDir = project(":all").projectDir
val pluginConfigDir = appDir.resolve("plugins").resolve("resources")

val libsAlias = libs
val projectsAlias = projects

subprojects {
    val relative = projectDir.relativeTo(rootPluginDir)
    buildDir = rootPluginBuildDir.resolve(relative)
    group = "org.rsmod.plugins"

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(projectsAlias.game)
        implementation(libsAlias.kotlinScriptRuntime)
        implementation(libsAlias.kotlinCoroutinesCore)
        implementation(libsAlias.rsmodPathfinder)
    }

    tasks.register("install") {
        copyResources(project)
    }
}

tasks.register("install-plugins") {
    subprojects.forEach { project ->
        copyResources(project)
    }
}

tasks.register("install-plugins-overwrite") {
    subprojects.forEach { project ->
        copyResources(project, overwriteFiles = true)
    }
}

fun copyResources(project: Project, overwriteFiles: Boolean = false) {
    val relativePluginDir = project.projectDir.relativeTo(rootPluginDir)
    val pluginResourceFiles = project.sourceSets.main.get().resources.asFileTree
    if (pluginResourceFiles.isEmpty) return
    val configDirectory = pluginConfigDir.resolve(relativePluginDir)
    pluginResourceFiles.forEach { file ->
        val existingFile = configDirectory.resolve(file.name)
        if (existingFile.exists() && !overwriteFiles) {
            /* do not overwrite existing config files */
            return@forEach
        }
        copy {
            from(file)
            into(configDirectory)
        }
    }
}
