package org.rsmod.plugins.api.config.file

import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.config.GameConfig
import java.nio.file.Files
import javax.inject.Inject

class NamedConfigFileModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<NamedConfigFileMap>()
            .toProvider<NamedConfigFileMapProvider>()
            .`in`(scope)
    }
}

private class NamedConfigFileMapProvider @Inject constructor(
    private val config: GameConfig
) : Provider<NamedConfigFileMap> {

    override fun get() = NamedConfigFileMap().apply {
        /* add config file extensions */
        DefaultExtensions.ALL.forEach { this += it }

        /* load config resource folder files */
        val files = Files.list(config.pluginConfigPath)
        files.forEach { loadDirectory(it) }
    }
}
