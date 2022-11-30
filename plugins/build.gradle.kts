import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

allprojects {
    plugins.withType<JavaPlugin> {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.7.0")
            implementation(project(":game"))
            implementation(project(":game:plugins"))
        }
    }

    plugins.withType<KotlinPluginWrapper> {
        kotlin {
            explicitApi = ExplicitApiMode.Disabled
        }
    }
}
