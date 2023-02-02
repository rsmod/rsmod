package org.rsmod.plugins.api.cache.type

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.cache.type.enums.EnumTypeListProvider

public object CacheTypeListModule : AbstractModule() {

    override fun configure() {
        bind(EnumTypeList::class.java)
            .toProvider(EnumTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
