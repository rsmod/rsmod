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
import org.rsmod.plugins.api.cache.name.ui.ComponentNameLoader
import org.rsmod.plugins.api.cache.name.ui.ComponentNameMap
import org.rsmod.plugins.api.cache.name.ui.UserInterfaceNameLoader
import org.rsmod.plugins.api.cache.name.ui.UserInterfaceNameMap
import javax.inject.Inject

class NamedTypeModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<NamedTypeLoaderList>()
            .toProvider<NamedTypeLoaderListProvider>()
            .`in`(scope)
        bind<NpcNameMap>().`in`(scope)
        bind<ItemNameMap>().`in`(scope)
        bind<ObjectNameMap>().`in`(scope)
        bind<UserInterfaceNameMap>().`in`(scope)
        bind<ComponentNameMap>().`in`(scope)
    }
}

private class NamedTypeLoaderListProvider @Inject constructor(
    private val itemList: ItemNameLoader,
    private val npcList: NpcNameLoader,
    private val objList: ObjectNameLoader,
    private val compList: ComponentNameLoader,
    private val interfaceList: UserInterfaceNameLoader
) : Provider<NamedTypeLoaderList> {

    override fun get() = NamedTypeLoaderList().apply {
        register(itemList)
        register(npcList)
        register(objList)
        register(compList)
        register(interfaceList)
    }
}
