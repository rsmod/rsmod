version = "186.0-SNAPSHOT"

dependencies {
    implementation(project(":util"))
    implementation(project(":cache"))
    implementation(project(":net"))
    implementation("io.netty:netty-all:${NetVersions.NETTY}")
    api("io.guthix:jagex-bytebuf:${ProjectVersions.GUTHIX_BYTEBUF}")
    api("io.guthix:jagex-store-5:${ProjectVersions.JS5_STORE}")
}
