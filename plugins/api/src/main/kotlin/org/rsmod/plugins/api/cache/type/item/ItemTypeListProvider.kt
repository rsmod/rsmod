package org.rsmod.plugins.api.cache.type.item

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.cache.config.item.ItemTypeList
import org.rsmod.plugins.cache.config.item.ItemTypeLoader
import org.rsmod.plugins.cache.config.param.ParamTypeList
import jakarta.inject.Inject
import jakarta.inject.Provider

public class ItemTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache,
    private val params: ParamTypeList
) : Provider<ItemTypeList> {

    override fun get(): ItemTypeList {
        val types = ItemTypeLoader.load(cache, params)
        return ItemTypeList(types.associateBy { it.id })
    }
}
