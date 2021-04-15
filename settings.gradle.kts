import java.nio.file.Files
import java.nio.file.Path

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "rsmod"
include("util")
include("game")
include("plugins")
includePlugins(project(":plugins").projectDir.toPath())
include("all")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.4.0"
        id("org.jmailen.kotlinter") version "3.3.0"
    }
}

fun includePlugins(pluginPath: Path) {
    Files.walk(pluginPath).forEach {
        if (!Files.isDirectory(it)) {
            return@forEach
        }
        searchPlugin(pluginPath, it)
    }
}

fun searchPlugin(parent: Path, path: Path) {
    val hasBuildFile = Files.exists(path.resolve("build.gradle.kts"))
    if (!hasBuildFile) {
        return
    }
    val relativePath = parent.relativize(path)
    val pluginName = relativePath.toString().replace(File.separator, ":")

    include("plugins:$pluginName")
}
