package org.rsmod.plugins.api.cache.type.npc

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import javax.inject.Inject
import javax.inject.Provider

public class NpcTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache,
    private val params: ParamTypeList
) : Provider<NpcTypeList> {

    override fun get(): NpcTypeList {
        val types = NpcTypeLoader.load(cache, params)
        return NpcTypeList(types.associateBy { it.id })
    }
}
