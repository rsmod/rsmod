plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)

    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
        optIn = listOf("kotlin.contracts.ExperimentalContracts")
    }
}
