dependencies {
    /* security dependencies */
    implementation("org.bouncycastle:bcprov-jdk15on:${SecurityVersions.BOUNCYCASTLE}")
    implementation("org.mindrot:jbcrypt:${SecurityVersions.BCRYPT}")

    /* jackson dependencies */
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${JacksonVersions.JACKSON}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${JacksonVersions.JACKSON}")
}

tasks.create<JavaExec>("createRsa") {
    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    main = "org.rsmod.util.RsaGenerator"
    args = listOf(
        "2048",
        "16",
        rootProject.projectDir.toPath().resolve("all/data/rsa/key.pem").toAbsolutePath().toString()
    )
}
