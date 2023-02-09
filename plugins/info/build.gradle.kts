plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh") apply true
}

jmh {
    profilers = listOf("stack")
}
