version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":util"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")
    implementation("io.netty:netty-buffer:${Versions.NETTY}")
}
