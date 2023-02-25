package org.rsmod.plugins.api.cache.type.enums

import com.google.inject.Provider
import javax.inject.Inject

public class EnumTypeListProvider @Inject constructor(
    private val loader: EnumTypeLoader
) : Provider<EnumTypeList> {

    override fun get(): EnumTypeList {
        return EnumTypeList(loader.load().associateBy { it.id })
    }
}
