package org.rsmod.plugins.api.cache.type.varp

import javax.inject.Inject
import javax.inject.Provider

public class VarpTypeListProvider @Inject constructor(
    private val loader: VarpTypeLoader
) : Provider<VarpTypeList> {

    override fun get(): VarpTypeList {
        return VarpTypeList(loader.load().associateBy { it.id })
    }
}
