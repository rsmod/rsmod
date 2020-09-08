dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":net"))
    implementation(project(":game"))
    implementation(project(":plugins"))
    findPlugins(project(":plugins")).forEach {
        implementation(it)
    }

    implementation("io.netty:netty-all:${NetVersions.NETTY}")
}

fun findPlugins(pluginProject: ProjectDependency): List<Project> {
    val plugins = mutableListOf<Project>()
    pluginProject.dependencyProject.subprojects.forEach {
        if (it.buildFile.exists()) {
            plugins.add(it)
        }
    }
    return plugins
}
