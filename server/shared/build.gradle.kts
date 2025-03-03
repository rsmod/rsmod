plugins {
    id("base-conventions")
}

dependencies {
    findValidApiSubProjects().forEach { api(it) }
    findPlugins().forEach { api(it) }
    implementation(libs.classgraph)
    implementation(libs.guice)
    implementation(libs.kotlin.reflect)
    implementation(libs.openrs2.cache)
    implementation(projects.api.gameProcess)
    implementation(projects.api.parsers.json)
    implementation(projects.engine.annotations)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
    implementation(projects.engine.plugin)
}

fun findPlugins(): List<Project> =
    project(":content").subprojects.filter { it.buildFile.exists() }

fun findValidApiSubProjects(): List<Project> =
    project(":api").subprojects.filter { it.buildFile.exists() && !filteredApiSubProject(it) }

fun filteredApiSubProject(project: Project): Boolean =
    project.name == "testing" || project.parent?.name == "testing"
