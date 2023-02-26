package org.rsmod.plugins.api.cache.type.npc

import com.google.inject.Provider
import javax.inject.Inject

public class NpcTypeListProvider @Inject constructor(
    private val loader: NpcTypeLoader
) : Provider<NpcTypeList> {

    override fun get(): NpcTypeList {
        return NpcTypeList(loader.load().associateBy { it.id })
    }
}