package org.rsmod.plugins.api.cache.map.xtea

import jakarta.inject.Inject
import jakarta.inject.Provider

public class XteaRepositoryProvider @Inject constructor(
    private val loader: XteaRepositoryLoader
) : Provider<XteaRepository> {

    override fun get(): XteaRepository {
        return loader.load()
    }
}
