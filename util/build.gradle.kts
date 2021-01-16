dependencies {
    implementation("org.bouncycastle:bcprov-jdk15on:${Versions.BOUNCYCASTLE}")
    implementation("org.mindrot:jbcrypt:${Versions.BCRYPT}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.JACKSON}")
}

tasks.create<JavaExec>("createRsa") {
    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    main = "org.rsmod.util.security.RsaGenerator"
    args = listOf(
        "2048",
        "16",
        rootProject.projectDir.toPath().resolve("all/data/rsa/key.pem").toAbsolutePath().toString()
    )
}
