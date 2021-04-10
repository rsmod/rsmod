package org.rsmod.plugins.api.cache.config.file

import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.plugins.api.cache.toResourceUrl
import java.io.File
import java.io.IOException

private const val CONFIG_RESOURCE_FOLDER = "/config/"

class NamedConfigFileModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<NamedConfigFileMap>()
            .toProvider<NamedConfigFileMapProvider>()
            .`in`(scope)
    }
}

private class NamedConfigFileMapProvider : Provider<NamedConfigFileMap> {

    override fun get() = NamedConfigFileMap().apply {
        /* add config file extensions */
        DefaultExtensions.ALL.forEach { this += it }

        /* load config resource folder files */
        val url = CONFIG_RESOURCE_FOLDER.toResourceUrl() ?: return@apply
        val files = File(url.path).listFiles() ?: throw IOException("Error listing files for resource path: ${url.path}")
        files.forEach { loadDirectory(it.toPath()) }
    }
}
