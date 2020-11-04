version = ProjectVersions.RS_MOD_API

dependencies {
    implementation(project(":util"))
    implementation(project(":net"))
    implementation("io.netty:netty-all:${NetVersions.NETTY}")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:${ProjectVersions.KOTLIN_RETRY}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${JacksonVersions.JACKSON}")
    implementation("io.guthix:jagex-bytebuf:${ProjectVersions.GUTHIX_BYTEBUF}")
    implementation("io.guthix:jagex-store-5:${ProjectVersions.JS5_STORE}")
}
