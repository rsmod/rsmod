package org.rsmod.plugins.api.cache.type.item

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import javax.inject.Inject
import javax.inject.Provider

public class ItemTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache,
    private val params: ParamTypeList
) : Provider<ItemTypeList> {

    override fun get(): ItemTypeList {
        val types = ItemTypeLoader.load(cache, params)
        return ItemTypeList(types.associateBy { it.id })
    }
}
