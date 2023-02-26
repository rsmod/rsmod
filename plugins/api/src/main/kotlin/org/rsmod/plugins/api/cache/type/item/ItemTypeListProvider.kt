package org.rsmod.plugins.api.cache.type.item

import javax.inject.Inject
import javax.inject.Provider

public class ItemTypeListProvider @Inject constructor(
    private val loader: ItemTypeLoader
) : Provider<ItemTypeList> {

    override fun get(): ItemTypeList {
        return ItemTypeList(loader.load().associateBy { it.id })
    }
}
