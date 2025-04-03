plugins {
    kotlin("jvm")
}

plugins.withType<JavaPlugin> {
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withJavadocJar()
        withSourcesJar()
    }
}

kotlin {
    jvmToolchain(11)

    compilerOptions {
        optIn = listOf("kotlin.contracts.ExperimentalContracts")
    }
}
