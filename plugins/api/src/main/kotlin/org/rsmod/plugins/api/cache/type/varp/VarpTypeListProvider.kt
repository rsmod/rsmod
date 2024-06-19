package org.rsmod.plugins.api.cache.type.varp

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.cache.config.varp.VarpTypeList
import org.rsmod.plugins.cache.config.varp.VarpTypeLoader
import jakarta.inject.Inject
import jakarta.inject.Provider

public class VarpTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache
) : Provider<VarpTypeList> {

    override fun get(): VarpTypeList {
        val types = VarpTypeLoader.load(cache)
        return VarpTypeList(types.associateBy { it.id })
    }
}
