import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

subprojects {
    plugins.withType<KotlinPluginWrapper> {
        dependencies {
            implementation(projects.game.types)
            implementation(projects.plugins.api)
            implementation(projects.plugins.typesGenerated)
        }
    }
}
