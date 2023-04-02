package org.rsmod.plugins.api.cache.type

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.plugins.api.cache.type.enums.EnumTypeListProvider
import org.rsmod.plugins.api.cache.type.item.ItemTypeListProvider
import org.rsmod.plugins.api.cache.type.npc.NpcTypeListProvider
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeListProvider
import org.rsmod.plugins.api.cache.type.param.ParamTypeListProvider
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeListProvider
import org.rsmod.plugins.api.cache.type.varp.VarpTypeListProvider
import org.rsmod.plugins.cache.config.enums.EnumTypeList
import org.rsmod.plugins.cache.config.item.ItemTypeList
import org.rsmod.plugins.cache.config.npc.NpcTypeList
import org.rsmod.plugins.cache.config.obj.ObjectTypeList
import org.rsmod.plugins.cache.config.param.ParamTypeList
import org.rsmod.plugins.cache.config.varbit.VarbitTypeList
import org.rsmod.plugins.cache.config.varp.VarpTypeList

public object CacheTypeModule : AbstractModule() {

    override fun configure() {
        bind(EnumTypeList::class.java)
            .toProvider(EnumTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(ItemTypeList::class.java)
            .toProvider(ItemTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(NpcTypeList::class.java)
            .toProvider(NpcTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(ObjectTypeList::class.java)
            .toProvider(ObjectTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(ParamTypeList::class.java)
            .toProvider(ParamTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(VarpTypeList::class.java)
            .toProvider(VarpTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(VarbitTypeList::class.java)
            .toProvider(VarbitTypeListProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
