version = Versions.RS_MOD_API

dependencies {
    implementation(project(":util"))
    implementation(project(":net"))
    implementation("io.netty:netty-all:${Versions.NETTY}")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:${Versions.KOTLIN_RETRY}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")
    implementation("io.guthix:jagex-bytebuf:${Versions.GUTHIX_BYTEBUF}")
    implementation("io.guthix:jagex-store-5:${Versions.JS5_STORE}")
}
