package org.rsmod.plugins.api.cache.type.varbit

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.cache.config.varbit.VarbitTypeList
import org.rsmod.plugins.cache.config.varbit.VarbitTypeLoader
import jakarta.inject.Inject
import jakarta.inject.Provider

public class VarbitTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache
) : Provider<VarbitTypeList> {

    override fun get(): VarbitTypeList {
        val types = VarbitTypeLoader.load(cache)
        return VarbitTypeList(types.associateBy { it.id })
    }
}
