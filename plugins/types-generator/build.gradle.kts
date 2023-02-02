plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.buffer)
    implementation(projects.cache)
    implementation(projects.game.types)
    implementation(projects.plugins.api)
    implementation(projects.plugins.typesGenerated)
    implementation(projects.toml)
    implementation(libs.clikt)
    implementation(libs.guice)
    implementation(libs.openrs2Cache)
}

tasks.register<JavaExec>("generateTypeNames") {
    workingDir = rootProject.projectDir
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.rsmod.plugins.types.gen.GenerateTypesCommandKt")
}
