import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

subprojects {
    group = "org.rsmod.plugins.content"

    plugins.withType<KotlinPluginWrapper> {
        dependencies {
            implementation(projects.plugins.api)
        }
    }
}
