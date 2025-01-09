plugins {
    alias(libs.plugins.manes.versions)
    alias(libs.plugins.gradle.download)
    id("kotlin-conventions")
}

allprojects {
    group = "org.rsmod"
    version = "0.0.1"
}

dependencies {
    implementation(projects.server.install)
}

tasks.register("install") {
    group = "installation"
    description = "Runs the complete server installation task."

    dependsOn(":setupLogbackNovice")
    dependsOn(":downloadCache")
    dependsOn(":packCache")
    dependsOn(":generateRsa")

    doLast { logger.lifecycle("Installation process completed.") }
}

tasks.register<JavaExec>("downloadCache") {
    group = "installation"
    description = "Runs the cache download & extract task."

    args = getArgsFromProperty("cacheDownload")
    mainClass.set("org.rsmod.server.install.GameServerCacheDownloaderKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting the cache download process...") }
    doLast { logger.lifecycle("Cache download completed.") }
}

tasks.register<JavaExec>("packCache") {
    group = "installation"
    description = "Runs the cache packer task."

    args = getArgsFromProperty("cachePack")
    mainClass.set("org.rsmod.server.install.GameServerCachePackerKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting the cache-packing process...") }
    doLast { logger.lifecycle("Cache-packing process completed.") }
}

tasks.register<JavaExec>("generateRsa") {
    group = "installation"
    description = "Runs the rsa-key generation task."

    args = getArgsFromProperty("rsa")
    mainClass.set("org.rsmod.server.install.GameNetworkRsaGeneratorKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting the rsa-key generation process...") }
    doLast { logger.lifecycle("RSA generation process completed.") }
}

registerLogbackCopyTask(
    taskName = "setupLogbackNovice",
    sourceFileName = "logback.novice.xml",
    description = "Copy the novice logback format for use."
)

registerLogbackCopyTask(
    taskName = "setupLogbackAdvanced",
    sourceFileName = "logback.advanced.xml",
    description = "Copy the advanced logback format for use."
)

fun registerLogbackCopyTask(taskName: String, sourceFileName: String, description: String) {
    tasks.register<Copy>(taskName) {
        this.description = description

        val app = project(":server:app")
        val appDir = app.projectDir

        from("$appDir/src/main/resources/$sourceFileName")
        into("$appDir/src/main/resources/")
        rename(sourceFileName, "logback.xml")
    }
}

fun getArgsFromProperty(propertyName: String): List<String> {
    val argsProp = project.findProperty(propertyName)
    return argsProp?.toString()?.split(" ") ?: emptyList()
}
