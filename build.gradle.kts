import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jmailen.gradle.kotlinter.KotlinterExtension
import org.jmailen.gradle.kotlinter.KotlinterPlugin

plugins {
    kotlin("jvm") version Versions.KOTLIN
    id("org.jmailen.kotlinter") version Versions.KOTLINTER apply false
}

allprojects {
    group = "org.rsmod"
    version = Versions.RS_MOD

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
        implementation("com.google.inject:guice:${Versions.GUICE}")
        implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:${Versions.KOTLIN_GUICE}")
        implementation("org.slf4j:slf4j-api:${Versions.SL4J}")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:${Versions.LOG4J}")
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:${Versions.INLINE_LOGGER}")
        testImplementation("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
    }

    java {
        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = Versions.JVM
            kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = Versions.JVM
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
