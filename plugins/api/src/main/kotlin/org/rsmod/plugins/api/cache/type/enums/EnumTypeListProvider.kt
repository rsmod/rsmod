package org.rsmod.plugins.api.cache.type.enums

import javax.inject.Inject
import javax.inject.Provider

public class EnumTypeListProvider @Inject constructor(
    private val loader: EnumTypeLoader
) : Provider<EnumTypeList> {

    override fun get(): EnumTypeList {
        return EnumTypeList(loader.load().associateBy { it.id })
    }
}
