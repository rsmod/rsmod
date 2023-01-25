plugins {
    kotlin("jvm")
}

@Suppress("UnstableApiUsage")
dependencies {
    api(libs.openrs2Crypto)
	implementation(projects.toml)
}
