package org.rsmod.plugins.api.cache.type.enums

import com.google.inject.Provider
import javax.inject.Inject

public class EnumTypeListProvider @Inject constructor(
    private val loader: EnumTypeLoader
) : Provider<EnumTypeList> {

    override fun get(): EnumTypeList {
        val types = loader.load()
        val elements = arrayOfNulls<EnumType<Any, Any>>(types.maxOf { it.id } + 1)
        types.forEach { type -> elements[type.id] = type }
        return EnumTypeList(elements)
    }
}
