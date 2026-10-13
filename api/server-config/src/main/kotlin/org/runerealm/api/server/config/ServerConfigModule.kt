package org.runerealm.api.server.config

import com.google.inject.Provider
import jakarta.inject.Inject
import java.nio.file.Path
import java.nio.file.Paths
import org.runerealm.module.ExtendedModule

public object ServerConfigModule : ExtendedModule() {
    private val configFile: Path
        get() = Paths.get(".data", "server.toml")

    override fun bind() {
        bindInstance<ServerConfigLoader>()
        bindProvider(ServerConfigProvider::class.java)
    }

    private class ServerConfigProvider @Inject constructor(private val loader: ServerConfigLoader) :
        Provider<ServerConfig> {
        override fun get(): ServerConfig = loader.loadOrCreate(configFile)
    }
}
