version = "1.1.2"

plugins {
    `maven-publish`
    signing
    kotlin("jvm")
    id("me.champeau.gradle.jmh") apply true
}

@Suppress("UnstableApiUsage")
dependencies {
    jmh("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    jmh("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
}

jmh {
    profilers = listOf("stack")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            packaging = "jar"
            name.set("RS Mod Pathfinder")
            description.set(
                """
                A custom BFS pathfinder implementation to emulate RS.
                """.trimIndent()
            )
        }
        signing {
            useGpgCmd()
            sign(publishing.publications["maven"])
        }
    }
}
