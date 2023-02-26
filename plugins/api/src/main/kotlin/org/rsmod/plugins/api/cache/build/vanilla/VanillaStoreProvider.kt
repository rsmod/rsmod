package org.rsmod.plugins.api.cache.build.vanilla

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.vanillaCachePath
import java.nio.file.Files
import javax.inject.Inject
import javax.inject.Provider

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
