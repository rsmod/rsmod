package org.rsmod.plugins.api.cache.type.varbit

import com.google.inject.Provider
import javax.inject.Inject

public class VarbitTypeListProvider @Inject constructor(
    private val loader: VarbitTypeLoader
) : Provider<VarbitTypeList> {

    override fun get(): VarbitTypeList {
        return VarbitTypeList(loader.load().associateBy { it.id })
    }
}
