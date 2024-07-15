package org.rsmod.plugins.api.cache.build.game

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cache.build.StoreProviderUtil
import org.rsmod.plugins.api.gameCachePath
import org.rsmod.plugins.api.vanillaCachePath
import jakarta.inject.Inject
import jakarta.inject.Provider

public class GameStoreProvider @Inject constructor(
    private val alloc: ByteBufAllocator,
    private val config: GameConfig
) : Provider<Store> {

    private val path = config.gameCachePath

    override fun get(): Store {
        StoreProviderUtil.createOrCopyStore(
            createPath = path.toFile(),
            copyPath = config.vanillaCachePath.toFile()
        )
        return Store.open(path, alloc)
    }
}
