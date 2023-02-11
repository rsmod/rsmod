package org.rsmod.game.cache

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider

public class StoreProvider @Inject constructor(
    private val alloc: ByteBufAllocator,
    @CachePath private val path: Path
) : Provider<Store> {

    override fun get(): Store {
        if (!Files.exists(path)) Files.createDirectories(path)
        return Store.open(path, alloc)
    }
}
