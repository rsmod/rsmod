import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jmailen.gradle.kotlinter.KotlinterExtension
import org.jmailen.gradle.kotlinter.KotlinterPlugin

plugins {
    kotlin("jvm") version JvmVersions.KOTLIN
    id("org.jmailen.kotlinter") version JvmVersions.KOTLINTER apply false
}

allprojects {
    group = "org.rsmod"
    version = ProjectVersions.RS_MOD

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
        implementation("com.google.inject:guice:${DependencyInjectionVersions.GUICE}")
        implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:${DependencyInjectionVersions.KOTLIN_GUICE}")
        implementation("org.slf4j:slf4j-api:${LoggerVersions.SL4J}")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:${LoggerVersions.LOG4J}")
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:${LoggerVersions.INLINE_LOGGER}")
        testImplementation("org.junit.jupiter:junit-jupiter:${TestVersions.JUNIT}")
    }

    java {
        sourceCompatibility = JvmVersions.JAVA
        targetCompatibility = JvmVersions.JAVA
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = JvmVersions.JVM
            kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = JvmVersions.JVM
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
