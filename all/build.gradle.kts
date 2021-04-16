plugins {
    application
}

application {
    mainClass.set("org.rsmod.Server")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(projects.util)
    implementation(projects.game)
    implementation(projects.plugins)
    findPlugins(projects.plugins).forEach {
        implementation(it)
    }

    implementation(libs.nettyAll)
    implementation(libs.jacksonKotlin)
    implementation(libs.kotlinCoroutinesCore)
}

tasks.register<JavaExec>("cache-pack") {
    main = "org.rsmod.plugins.api.cache.packer.ConfigTypePacker"
    classpath = sourceSets.main.get().runtimeClasspath
    args = emptyList()
}

tasks.register<JavaExec>("pack-items") {
    main = "org.rsmod.plugins.api.cache.packer.ConfigTypePacker"
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf("-item")
}

tasks.register<JavaExec>("pack-npcs") {
    main = "org.rsmod.plugins.api.cache.packer.ConfigTypePacker"
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf("-npc")
}

tasks.register<JavaExec>("pack-objs") {
    main = "org.rsmod.plugins.api.cache.packer.ConfigTypePacker"
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf("-objs")
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
