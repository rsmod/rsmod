package org.rsmod.plugins.api.cache.type.param

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.cache.config.param.ParamTypeList
import org.rsmod.plugins.cache.config.param.ParamTypeLoader
import jakarta.inject.Inject
import jakarta.inject.Provider

public class ParamTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache
) : Provider<ParamTypeList> {

    override fun get(): ParamTypeList {
        val types = ParamTypeLoader.load(cache)
        return ParamTypeList(types.associateBy { it.id })
    }
}
