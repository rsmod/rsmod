subprojects {
    group = "gg.rsmod.plugins.core"

    dependencies {
        implementation(project(":util"))
        implementation(project(":plugins:api"))
        implementation(project(":plugins:protocol"))
    }
}
