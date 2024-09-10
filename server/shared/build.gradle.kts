plugins {
    id("base-conventions")
}

dependencies {
    api(projects.api.config)
    api(projects.api.type.typeScriptDsl)
    api(projects.api.type.typeSymbols)
    api(projects.api.type.typeBuilders)
    api(projects.api.type.typeEditors)
    api(projects.api.type.typeReferences)
    api(projects.api.type.typeResolver)
    api(projects.api.type.typeUpdater)
    api(projects.api.type.typeVerifier)
    findPlugins(projects.content).forEach {
        api(it)
    }
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

fun findPlugins(pluginProject: ProjectDependency): List<Project> {
    val plugins = mutableListOf<Project>()
    for (project in pluginProject.dependencyProject.subprojects) {
        if (project.buildFile.exists()) {
            plugins += project
        }
    }
    return plugins
}