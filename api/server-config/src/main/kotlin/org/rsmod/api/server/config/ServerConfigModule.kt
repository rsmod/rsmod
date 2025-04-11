package org.rsmod.api.server.config

import com.google.inject.Provider
import com.google.inject.Provides
import jakarta.inject.Inject
import java.nio.file.Path
import java.nio.file.Paths
import org.rsmod.module.ExtendedModule

public object ServerConfigModule : ExtendedModule() {
    private val configFile: Path
        get() = Paths.get(".data", "server.toml")

    override fun bind() {
        bindInstance<ServerConfigLoader>()
        bindProvider(ServerConfigProvider::class.java)
    }

    @Provides public fun provideWorldConfig(parent: ServerConfig): WorldConfig = parent.world

    @Provides public fun provideGameConfig(parent: ServerConfig): GameConfig = parent.game

    @Provides public fun provideMetaConfig(parent: ServerConfig): MetaConfig = parent.meta

    private class ServerConfigProvider @Inject constructor(private val loader: ServerConfigLoader) :
        Provider<ServerConfig> {
        override fun get(): ServerConfig = loader.loadOrCreate(configFile)
    }
}
