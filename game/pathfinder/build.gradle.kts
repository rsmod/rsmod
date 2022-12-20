plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh")
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
