package org.rsmod.plugins.api.cache.name

import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.name.NamedTypeLoaderList
import org.rsmod.plugins.api.cache.name.item.ItemNameLoader
import org.rsmod.plugins.api.cache.name.item.ItemNameMap
import org.rsmod.plugins.api.cache.name.npc.NpcNameLoader
import org.rsmod.plugins.api.cache.name.npc.NpcNameMap
import org.rsmod.plugins.api.cache.name.obj.ObjectNameLoader
import org.rsmod.plugins.api.cache.name.obj.ObjectNameMap
import javax.inject.Inject

class NamedTypeModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<NpcNameMap>().`in`(scope)
        bind<ItemNameMap>().`in`(scope)
        bind<ObjectNameMap>().`in`(scope)
        bind<NamedTypeLoaderList>()
            .toProvider<NamedTypeLoaderListProvider>()
            .`in`(scope)
    }
}

private class NamedTypeLoaderListProvider @Inject constructor(
    private val itemList: ItemNameLoader,
    private val npcList: NpcNameLoader,
    private val objList: ObjectNameLoader
) : Provider<NamedTypeLoaderList> {

    override fun get() = NamedTypeLoaderList().apply {
        -itemList
        -npcList
        -objList
    }
}
