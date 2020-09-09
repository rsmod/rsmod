version = "186.0-SNAPSHOT"

dependencies {
    implementation(project(":util"))
    implementation(project(":cache"))
    implementation(project(":net"))
    implementation("io.netty:netty-all:${NetVersions.NETTY}")
    api("com.github.guthix:jagex-bytebuf:${ProjectVersions.GUTHIX_BYTEBUF_VERSION}")

    implementation("com.github.runelite.runelite:cache:runelite-parent-1.5.2.1")
}
