package org.rsmod.plugins.types.gen

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider

private val CACHE_PATH = Path.of(".data/cache/packed")

class StoreProvider @Inject constructor(
    private val alloc: ByteBufAllocator
) : Provider<Store> {

    override fun get(): Store {
        return Store.open(CACHE_PATH, alloc)
    }
}
