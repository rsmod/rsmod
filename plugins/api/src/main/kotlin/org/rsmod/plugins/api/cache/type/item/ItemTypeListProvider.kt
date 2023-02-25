package org.rsmod.plugins.api.cache.type.item

import com.google.inject.Provider
import javax.inject.Inject

public class ItemTypeListProvider @Inject constructor(
    private val loader: ItemTypeLoader
) : Provider<ItemTypeList> {

    override fun get(): ItemTypeList {
        return ItemTypeList(loader.load().associateBy { it.id })
    }
}
