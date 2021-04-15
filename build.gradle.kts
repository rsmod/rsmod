import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.KotlinterExtension
import org.jmailen.gradle.kotlinter.KotlinterPlugin

val libsAlias = libs

plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter") apply false
}

allprojects {
    group = "org.rsmod"
    version = "0.0.1"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/michaelbull/maven")
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(libsAlias.kotlinReflect)
        implementation(libsAlias.guice)
        implementation(libsAlias.kotlinGuice)
        implementation(libsAlias.slf4j)
        implementation(libsAlias.inlineLogger)
        testImplementation(libsAlias.junit)
        testImplementation(libsAlias.junitEngine)
    }

    tasks.withType<JavaCompile> {
        options.release.set(11)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
    }

    plugins.withType<KotlinterPlugin> {
        configure<KotlinterExtension> {
            disabledRules = arrayOf(
                "filename",
                /* https://github.com/pinterest/ktlint/issues/764 */
                "parameter-list-wrapping",
                /* https://github.com/pinterest/ktlint/issues/527 */
                "import-ordering"
            )
        }
    }

    plugins.withType<KotlinPluginWrapper> {
        apply(plugin = "org.jmailen.kotlinter")
    }
}
