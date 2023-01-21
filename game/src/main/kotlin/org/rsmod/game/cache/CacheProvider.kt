package org.rsmod.game.cache

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import javax.inject.Inject
import javax.inject.Provider

public class CacheProvider @Inject constructor(
    private val store: Store,
    private val alloc: ByteBufAllocator
) : Provider<Cache> {

    override fun get(): Cache {
        return Cache.open(store, alloc)
    }
}
