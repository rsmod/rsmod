dependencies {
    implementation(libs.bcrypt)
    implementation(libs.bouncyCastle)
    implementation(libs.jacksonKotlin)
    implementation(libs.jacksonYaml)
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
