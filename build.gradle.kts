@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/* https://youtrack.jetbrains.com/issue/KTIJ-19369 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinter) apply false
}

allprojects {
    group = "org.rsmod"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    plugins.withType<ApplicationPlugin> {
        tasks.named<JavaExec>("run") {
            standardInput = System.`in`
            workingDir = rootDir
        }
    }

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            withSourcesJar()
        }

        dependencies {
            testImplementation(libs.junitApi)
            testRuntimeOnly(libs.junitEngine)
            testImplementation(libs.junitParams)
        }
    }

    plugins.withType<KotlinPluginWrapper> {
        apply(plugin = "org.jmailen.kotlinter")

        dependencies {
            api("org.jetbrains.kotlin:kotlin-stdlib-common") { strictKotlinVersion() }
            api("org.jetbrains.kotlin:kotlin-stdlib-jdk7") { strictKotlinVersion() }
            api("org.jetbrains.kotlin:kotlin-stdlib-jdk8") { strictKotlinVersion() }
            api("org.jetbrains.kotlin:kotlin-stdlib") { strictKotlinVersion() }
            implementation(libs.guice)
            implementation(libs.logback)
            implementation(libs.inlineLogger)
        }

        kotlin {
            explicitApi()
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(11)
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        jvmArgs = listOf("-ea")
        useJUnitPlatform()
    }
}

fun ExternalModuleDependency.strictKotlinVersion(): ExternalModuleDependency {
    version { strictly(project.getKotlinPluginVersion()) }
    return this
}
