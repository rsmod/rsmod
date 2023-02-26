package org.rsmod.plugins.api.cache.type.obj

import javax.inject.Inject
import javax.inject.Provider

public class ObjectTypeListProvider @Inject constructor(
    private val loader: ObjectTypeLoader
) : Provider<ObjectTypeList> {

    override fun get(): ObjectTypeList {
        return ObjectTypeList(loader.load().associateBy { it.id })
    }
}
