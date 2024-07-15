package org.rsmod.plugins.api.cache.type.obj

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.cache.config.obj.ObjectTypeList
import org.rsmod.plugins.cache.config.obj.ObjectTypeLoader
import org.rsmod.plugins.cache.config.param.ParamTypeList
import jakarta.inject.Inject
import jakarta.inject.Provider

public class ObjectTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache,
    private val params: ParamTypeList
) : Provider<ObjectTypeList> {

    override fun get(): ObjectTypeList {
        val types = ObjectTypeLoader.load(cache, params)
        return ObjectTypeList(types.associateBy { it.id })
    }
}
