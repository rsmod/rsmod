package org.rsmod.game.cache

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import javax.inject.Inject
import javax.inject.Provider

public class StoreProvider @Inject constructor(
    private val alloc: ByteBufAllocator,
    private val config: GameConfig
) : Provider<Store> {

    override fun get(): Store {
        return Store.open(config.packedCachePath, alloc)
    }
}
