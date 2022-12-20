plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh")
}

dependencies {
    jmh("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
}

jmh {
    profilers = listOf("stack")
}
