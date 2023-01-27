plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.game)
    api(projects.game.plugins)
    implementation(projects.log)
    implementation(libs.guice)
    implementation(libs.logback)
    implementation(libs.inlineLogger)
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
