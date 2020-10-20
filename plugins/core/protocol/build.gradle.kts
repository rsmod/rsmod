version = ProjectVersions.PROTOCOL

dependencies {
    implementation(project(":util"))
    implementation(project(":cache"))
    implementation(project(":net"))
    implementation("io.netty:netty-all:${NetVersions.NETTY}")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:${ProjectVersions.KOTLIN_RETRY}")
    api("io.guthix:jagex-bytebuf:${ProjectVersions.GUTHIX_BYTEBUF}")
    api("io.guthix:jagex-store-5:${ProjectVersions.JS5_STORE}")
}
