import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jmailen.gradle.kotlinter.KotlinterExtension
import org.jmailen.gradle.kotlinter.KotlinterPlugin

plugins {
    kotlin("jvm")
}

plugins.withType<KotlinPluginWrapper> {
    kotlin {
        explicitApi = ExplicitApiMode.Disabled
    }

    dependencies {
        implementation(projects.plugins.types)
    }

    sourceSets {
        main {
            java {
                srcDirs("src/main/gen")
            }
        }
    }
}

plugins.withType<KotlinterPlugin> {
    configure<KotlinterExtension> {
        disabledRules = arrayOf("filename", "import-ordering")
    }
}
