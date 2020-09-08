dependencies {
    /* security dependencies */
    implementation("org.bouncycastle:bcprov-jdk15on:${SecurityVersions.BOUNCYCASTLE}")
}

tasks.create<JavaExec>("createRsa") {
    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    main = "gg.rsmod.util.RsaGenerator"
    args = listOf(
        "2048",
        "16",
        rootProject.projectDir.toPath().resolve("app/data/rsa/key.pem").toAbsolutePath().toString()
    )
}
