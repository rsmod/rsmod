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

        @Suppress("UnstableApiUsage")
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.7.0")
            implementation(project(":game"))
            implementation(project(":game:plugins"))
            implementation(libs.guice)
        }
    }
}
