plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.game)
    implementation(projects.game.events)
    implementation(projects.game.plugins)
    implementation(projects.log)
    implementation(libs.guice)
    implementation(libs.logback)
    implementation(libs.inlineLogger)
    implementation(libs.clikt)
    findPlugins(projects.plugins).forEach {
        implementation(it)
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
