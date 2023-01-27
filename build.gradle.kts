@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

/* https://youtrack.jetbrains.com/issue/KTIJ-19369 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.jmh) apply false
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
            testImplementation(libs.junitApi)
            testRuntimeOnly(libs.junitEngine)
            testImplementation(libs.junitParams)
        }
    }

    plugins.withType<KotlinPluginWrapper> {
        apply(plugin = "org.jmailen.kotlinter")

        dependencies {
            api("org.jetbrains.kotlin:kotlin-stdlib-common") { requireKotlinVersion() }
            api("org.jetbrains.kotlin:kotlin-stdlib-jdk7") { requireKotlinVersion() }
            api("org.jetbrains.kotlin:kotlin-stdlib-jdk8") { requireKotlinVersion() }
            api("org.jetbrains.kotlin:kotlin-stdlib") { requireKotlinVersion() }
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

fun ExternalModuleDependency.requireKotlinVersion(): ExternalModuleDependency {
    version { require(project.getKotlinPluginVersion()) }
    return this
}
