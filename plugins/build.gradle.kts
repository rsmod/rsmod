import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

subprojects {
    plugins.withType<KotlinPluginWrapper> {
        kotlin {
            explicitApi = ExplicitApiMode.Disabled
        }

        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.7.0")
            implementation(projects.game)
            implementation(projects.game.events)
            implementation(projects.game.plugins)
            implementation(projects.json)
            implementation(libs.logback)
            implementation(libs.inlineLogger)
        }
    }
}
