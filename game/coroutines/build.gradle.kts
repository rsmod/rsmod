version = "1.0.0"

plugins {
    `maven-publish`
    signing
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    testImplementation(libs.kotlinCoroutinesTest)
    implementation(libs.kotlinCoroutinesCore)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            packaging = "jar"
            name.set("RS Mod Coroutines")
            description.set(
                """
                A thin-layer interface for handling Kotlin coroutines in an RS-style game.
                """.trimIndent()
            )
        }
        signing {
            useGpgCmd()
            sign(publishing.publications["maven"])
        }
    }
}
