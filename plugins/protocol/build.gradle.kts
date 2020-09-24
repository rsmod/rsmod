version = ProjectVersions.PROTOCOL

dependencies {
    implementation(project(":util"))
    implementation(project(":cache"))
    implementation(project(":net"))
    implementation(project(":plugins:api"))
    implementation("io.netty:netty-all:${NetVersions.NETTY}")
    api("io.guthix:jagex-bytebuf:${ProjectVersions.GUTHIX_BYTEBUF}")
    api("io.guthix:jagex-store-5:${ProjectVersions.JS5_STORE}")
}
