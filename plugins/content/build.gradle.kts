val projectsAlias = projects

subprojects {
    group = "org.rsmod.plugins.content"

    dependencies {
        implementation(projectsAlias.plugins.api)
    }
}

