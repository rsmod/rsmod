version = "2.1.0"

plugins {
    `maven-publish`
    signing
    kotlin("jvm")
}

dependencies {
    testImplementation(libs.kotlinCoroutinesTest)
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
