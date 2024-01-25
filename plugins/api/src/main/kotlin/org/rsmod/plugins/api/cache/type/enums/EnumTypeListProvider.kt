package org.rsmod.plugins.api.cache.type.enums

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.cache.config.enums.EnumTypeList
import org.rsmod.plugins.cache.config.enums.EnumTypeLoader
import jakarta.inject.Inject
import jakarta.inject.Provider

public class EnumTypeListProvider @Inject constructor(
    @GameCache private val cache: Cache
) : Provider<EnumTypeList> {

    override fun get(): EnumTypeList {
        val types = EnumTypeLoader.load(cache)
        return EnumTypeList(types.associateBy { it.id })
    }
}
