version = "1.0.0"

plugins {
    `maven-publish`
    signing
    kotlin("jvm")
    id("me.champeau.gradle.jmh") apply true
}

dependencies {
    jmh(libs.kotlinCoroutinesCore)
}

jmh {
    profilers = listOf("stack")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            packaging = "jar"
            name.set("RS Mod Avatar-Info")
            description.set(
                """
                A plugin to handle avatar world information and convert into a format
                the client can comprehend.
                """.trimIndent()
            )
        }
        signing {
            useGpgCmd()
            sign(publishing.publications["maven"])
        }
    }
}
