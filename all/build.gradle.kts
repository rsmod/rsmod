plugins {
    application
}

application {
    mainClass.set("org.rsmod.Server")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":util"))
    implementation(project(":game"))
    implementation(project(":plugins"))
    findPlugins(project(":plugins")).forEach {
        implementation(it)
    }

    implementation(libs.nettyAll)
    implementation(libs.jacksonKotlin)
    implementation(libs.kotlinCoroutinesCore)
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
