package org.rsmod.plugins.api.cache.type.varp

import javax.inject.Provider
import javax.inject.Inject

public class VarpTypeListProvider @Inject constructor(
    private val loader: VarpTypeLoader
) : Provider<VarpTypeList> {

    override fun get(): VarpTypeList {
        return VarpTypeList(loader.load().associateBy { it.id })
    }
}
