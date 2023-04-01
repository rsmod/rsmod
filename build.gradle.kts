@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files

val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

/* https://youtrack.jetbrains.com/issue/KTIJ-19369 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    `jvm-test-suite`
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.jmh) apply false
    alias(libs.plugins.versions)
}

allprojects {
    group = "org.rsmod"
    version = "0.0.1"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.openrs2.org/repository/openrs2") }
        maven { url = uri("https://repo.openrs2.org/repository/openrs2-snapshots") }
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
            withJavadocJar()
            withSourcesJar()
        }

        dependencies {
            testRuntimeOnly(libs.junitEngine)
            testImplementation(libs.junitApi)
            testImplementation(libs.junitParams)
        }

        testing.suites {
            configureEach {
                if (this !is JvmTestSuite) error("Invalid test type: $this.")
                useJUnit()
                useJUnitJupiter()
            }
            @Suppress("UNUSED_VARIABLE")
            val integration by registering(JvmTestSuite::class)
        }
    }

    plugins.withType<KotlinPluginWrapper> {
        apply(plugin = "org.jmailen.kotlinter")
        kotlin.explicitApi()
    }

    tasks.withType<JavaCompile> {
        options.release.set(11)
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            /* https://youtrack.jetbrains.com/issue/KT-52735/Ignore-scripts-in-source-roots-by-default */
            freeCompilerArgs = listOf("-Xallow-any-scripts-in-source-roots")
        }
    }

    plugins.withType<MavenPublishPlugin> {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "rsmod"
                    if (version.toString().endsWith("-SNAPSHOT")) {
                        setUrl("https://oss.sonatype.org/content/repositories/snapshots")
                    } else {
                        setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    }
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }

            publications.withType<MavenPublication> {
                artifactId = "rsmod-${project.name}"
                pom {
                    url.set("https://github.com/rsmod")
                    inceptionYear.set("2022")

                    organization {
                        name.set("RS Mod")
                        url.set("https://github.com/rsmod")
                    }

                    licenses {
                        license {
                            name.set("ISC License")
                            url.set("https://opensource.org/licenses/isc-license.txt")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/rsmod/rsmod.git")
                        developerConnection.set("scm:git:git@github.com:github.com/rsmod/rsmod.git")
                        url.set("https://github.com/rsmod/rsmod")
                    }

                    developers {
                        developer {
                            name.set("Tomm")
                            url.set("https://github.com/Tomm0017")
                        }
                    }
                }
            }
        }
    }
}

tasks.register("install") {
    description = "Install RS Mod"
    dependsOn("createPlaceholderFiles")
    dependsOn(":plugins:installPlugins")
    dependsOn(":plugins:cache-packer:packConfigs")
    dependsOn(":plugins:types-generator:generateTypeNames")
}

tasks.register("createPlaceholderFiles") {
    doLast {
        val typeFiles = listOf(
            "Interfaces", "Components", "Items", "Npcs", "Objs",
            "Animations", "Graphics", "Enums", "Structs", "Params",
            "Invs", "Varps", "Varbits", "Scripts"
        )
        val outputProject = projects.plugins.typesGenerated.dependencyProject.buildFile.parentFile
        val outputPath = outputProject.resolve("src/main/gen/org/rsmod/types").toPath()
        if (!Files.isDirectory(outputPath)) Files.createDirectories(outputPath)
        typeFiles.forEach { typeFile ->
            val output = outputPath.resolve("$typeFile.kt")
            if (Files.exists(output)) return@forEach
            val string = "package org.rsmod.types\n\nobject $typeFile"
            Files.writeString(output, string)
        }
    }
}

fun ExternalModuleDependency.requireKotlinVersion(): ExternalModuleDependency {
    version { require(project.getKotlinPluginVersion()) }
    return this
}
