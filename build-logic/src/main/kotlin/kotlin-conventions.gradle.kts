plugins {
    kotlin("jvm")
}

plugins.withType<JavaPlugin> {
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        withJavadocJar()
        withSourcesJar()
    }
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        optIn = listOf("kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs = listOf("-Xnested-type-aliases")
    }
}
