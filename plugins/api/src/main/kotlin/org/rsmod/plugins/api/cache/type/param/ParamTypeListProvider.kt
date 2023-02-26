package org.rsmod.plugins.api.cache.type.param

import javax.inject.Inject
import javax.inject.Provider

public class ParamTypeListProvider @Inject constructor(
    private val loader: ParamTypeLoader
) : Provider<ParamTypeList> {

    override fun get(): ParamTypeList {
        return ParamTypeList(loader.load().associateBy { it.id })
    }
}
