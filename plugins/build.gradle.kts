// TODO: move to gradle.properties and somehow make sure it matches GameConfig path
val pluginConfigDir = rootDir.resolve(".data").resolve("plugins").resolve("configs")

plugins {
    kotlin("jvm")
}

tasks.register("installPlugins") {
    subprojects.forEach { project ->
        copyResources(project, pluginConfigDir)
    }
}

tasks.register("installPluginsFresh") {
    file(pluginConfigDir).deleteRecursively()
    subprojects.forEach { project ->
        copyResources(project, pluginConfigDir)
    }
}

fun copyResources(project: Project, outDir: File) {
    val relativePluginDir = project.projectDir.relativeTo(projectDir)
    val pluginResourceFiles = project.sourceSets.main.get().resources.sourceDirectories
    if (pluginResourceFiles.isEmpty) return
    val pluginOutDir = outDir.resolve(relativePluginDir)
    pluginResourceFiles.forEach { file ->
        val exportDir = file.resolve("export")
        if (!exportDir.exists()) return@forEach
        val existingFile = pluginOutDir.resolve(file.name)
        if (existingFile.exists()) {
            /* do not overwrite existing config files */
            return@forEach
        }
        copy {
            from(exportDir)
            into(pluginOutDir)
        }
    }
}
