import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

subprojects {
    group = rootProject.group.toString() + ".plugins"

    plugins.withType<KotlinPluginWrapper> {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.7.0")
            implementation(projects.game)
            implementation(projects.game.events)
            implementation(projects.game.plugins)
            implementation(libs.logback)
            implementation(libs.inlineLogger)
        }
    }
}
