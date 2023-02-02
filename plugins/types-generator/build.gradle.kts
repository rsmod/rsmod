plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.cache)
    implementation(projects.game.types)
    implementation(projects.plugins.api)
    implementation(projects.plugins.typesGenerated)
    implementation(projects.toml)
    implementation(libs.clikt)
    implementation(libs.guice)
    implementation(libs.openrs2Cache)
    implementation("org.reflections:reflections:0.10.2")
    /* include all plugins' classpath for reflection */
    findPlugins(projects.plugins).forEach {
        runtimeOnly(it)
    }
}

tasks.register<JavaExec>("generateTypeNames") {
    workingDir = rootProject.projectDir
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.rsmod.plugins.types.gen.GenerateTypesCommandKt")
}

fun findPlugins(pluginProject: ProjectDependency): MutableList<Project> {
    val plugins = mutableListOf<Project>()
    pluginProject.dependencyProject.subprojects.forEach {
        if (it != project && it.buildFile.exists()) {
            plugins += it
        }
    }
    return plugins
}
