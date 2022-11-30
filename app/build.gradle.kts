plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":game"))
    api(project(":game:plugins"))
    findPlugins(projects.plugins).forEach {
        api(it)
    }
}

fun findPlugins(pluginProject: ProjectDependency): List<Project> {
    val plugins = mutableListOf<Project>()
    pluginProject.dependencyProject.subprojects.forEach {
        if (it.buildFile.exists()) {
            plugins += it
        }
    }
    return plugins
}
