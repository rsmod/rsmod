plugins {
    id("base-conventions")
    id("benchmark-suite")
    id("publish-conventions")
}

version = "6.0.0"
description = "A custom BFS pathfinder implementation to emulate RS."

kotlin {
    explicitApi()
}

dependencies {
    testImplementation(libs.jackson.databind)
    jmh(libs.jackson.databind)
    jmh(libs.kotlin.coroutines.core)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        groupId = project.group.toString()
        artifactId = "rsmod-routefinder"
        version = project.version.toString()
        pom {
            packaging = "jar"
            name.set("RS Mod Routefinder")
            description.set(" A custom BFS pathfinder implementation to emulate RS.")
        }
    }
}
