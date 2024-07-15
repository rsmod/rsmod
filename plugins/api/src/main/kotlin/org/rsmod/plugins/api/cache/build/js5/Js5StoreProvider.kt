package org.rsmod.plugins.api.cache.build.js5

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cache.build.StoreProviderUtil
import org.rsmod.plugins.api.js5CachePath
import org.rsmod.plugins.api.vanillaCachePath
import jakarta.inject.Inject
import jakarta.inject.Provider

public class Js5StoreProvider @Inject constructor(
    private val alloc: ByteBufAllocator,
    private val config: GameConfig
) : Provider<Store> {

    private val path = config.js5CachePath

    override fun get(): Store {
        StoreProviderUtil.createOrCopyStore(
            createPath = path.toFile(),
            copyPath = config.vanillaCachePath.toFile()
        )
        return Store.open(path, alloc)
    }
}
