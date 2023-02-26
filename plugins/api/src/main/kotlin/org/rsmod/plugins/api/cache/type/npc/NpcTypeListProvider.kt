package org.rsmod.plugins.api.cache.type.npc

import javax.inject.Inject
import javax.inject.Provider

public class NpcTypeListProvider @Inject constructor(
    private val loader: NpcTypeLoader
) : Provider<NpcTypeList> {

    override fun get(): NpcTypeList {
        return NpcTypeList(loader.load().associateBy { it.id })
    }
}
