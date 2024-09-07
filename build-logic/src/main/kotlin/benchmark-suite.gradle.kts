val catalogs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val jmhPluginVersion by lazy { catalogs.findVersion("jmh").get().requiredVersion }

plugins {
    id("me.champeau.jmh")
}

jmh {
    jmhVersion.set(jmhPluginVersion)
    profilers.set(listOf("stack"))
    failOnError.set(true)
}
