package org.rsmod.plugins.api.cache.type.obj

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import javax.inject.Inject
import javax.inject.Provider

public class ObjectTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache,
    private val params: ParamTypeList
) : Provider<ObjectTypeList> {

    override fun get(): ObjectTypeList {
        val types = ObjectTypeLoader.load(cache, params)
        return ObjectTypeList(types.associateBy { it.id })
    }
}
