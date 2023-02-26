package org.rsmod.plugins.api.cache.type.varbit

import javax.inject.Inject
import javax.inject.Provider

public class VarbitTypeListProvider @Inject constructor(
    private val loader: VarbitTypeLoader
) : Provider<VarbitTypeList> {

    override fun get(): VarbitTypeList {
        return VarbitTypeList(loader.load().associateBy { it.id })
    }
}
