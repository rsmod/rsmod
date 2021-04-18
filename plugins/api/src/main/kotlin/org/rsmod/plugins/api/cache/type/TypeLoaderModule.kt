package org.rsmod.plugins.api.cache.type

import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.cache.type.CacheTypeLoaderList
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.game.model.vars.type.VarbitTypeList
import org.rsmod.game.model.vars.type.VarpTypeList
import org.rsmod.plugins.api.cache.type.item.ItemTypeLoader
import org.rsmod.plugins.api.cache.type.npc.NpcTypeLoader
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeLoader
import org.rsmod.plugins.api.cache.type.vars.VarbitTypeLoader
import org.rsmod.plugins.api.cache.type.vars.VarpTypeLoader
import javax.inject.Inject

class TypeLoaderModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<CacheTypeLoaderList>()
            .toProvider<CacheTypeLoaderListProvider>()
            .`in`(scope)
        bind<NpcTypeList>().`in`(scope)
        bind<ObjectTypeList>().`in`(scope)
        bind<ItemTypeList>().`in`(scope)
        bind<VarpTypeList>().`in`(scope)
        bind<VarbitTypeList>().`in`(scope)
    }
}

private class CacheTypeLoaderListProvider @Inject constructor(
    private val npcLoader: NpcTypeLoader,
    private val objectLoader: ObjectTypeLoader,
    private val itemLoader: ItemTypeLoader,
    private val varpLoader: VarpTypeLoader,
    private val varbitLoader: VarbitTypeLoader
) : Provider<CacheTypeLoaderList> {

    override fun get(): CacheTypeLoaderList {
        return CacheTypeLoaderList().apply {
            register(npcLoader)
            register(objectLoader)
            register(itemLoader)
            register(varpLoader)
            register(varbitLoader)
        }
    }
}
