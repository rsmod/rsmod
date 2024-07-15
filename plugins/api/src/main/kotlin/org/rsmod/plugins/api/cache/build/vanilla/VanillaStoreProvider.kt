package org.rsmod.plugins.api.cache.build.vanilla

import io.netty.buffer.ByteBufAllocator
import jakarta.inject.Inject
import jakarta.inject.Provider
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.vanillaCachePath
import java.nio.file.Files

public class VanillaStoreProvider @Inject constructor(
    private val alloc: ByteBufAllocator,
    config: GameConfig
) : Provider<Store> {

    private val path = config.vanillaCachePath

    override fun get(): Store {
        if (!Files.exists(path)) Files.createDirectories(path)
        return Store.open(path, alloc)
    }
}
