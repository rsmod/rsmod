package org.rsmod.plugins.api.cache.type.obj

import com.google.inject.Provider
import javax.inject.Inject

public class ObjectTypeListProvider @Inject constructor(
    private val loader: ObjectTypeLoader
) : Provider<ObjectTypeList> {

    override fun get(): ObjectTypeList {
        return ObjectTypeList(loader.load().associateBy { it.id })
    }
}
