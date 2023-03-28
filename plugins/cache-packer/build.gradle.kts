plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.game)
    implementation(projects.plugins.api)
    implementation(projects.plugins.cache)
    implementation(projects.plugins.types)
    implementation(projects.toml)
    implementation(libs.clikt)
    implementation(libs.inlineLogger)
    implementation(libs.openrs2Cache)
}

tasks.register<JavaExec>("packConfigs") {
    workingDir = rootProject.projectDir
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.rsmod.plugins.cache.packer.ConfigPackerCommandKt")
}
