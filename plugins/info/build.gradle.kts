version = "1.0.0"

plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh") apply true
}

jmh {
    profilers = listOf("stack")
}
