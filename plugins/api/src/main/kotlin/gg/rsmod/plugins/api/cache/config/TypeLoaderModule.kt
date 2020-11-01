package gg.rsmod.plugins.api.cache.config

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.cache.ConfigTypeLoaderMap
import gg.rsmod.game.model.item.ItemType
import gg.rsmod.game.model.item.ItemTypeList
import gg.rsmod.game.model.obj.ObjectType
import gg.rsmod.game.model.obj.ObjectTypeList
import gg.rsmod.plugins.api.cache.config.item.ItemTypeLoader
import gg.rsmod.plugins.api.cache.config.obj.ObjectTypeLoader

class TypeLoaderModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<ConfigTypeLoaderMap>()
            .toProvider<ConfigTypeLoaderMapProvider>()
            .`in`(scope)

        bind<ObjectTypeList>()
            .`in`(scope)

        bind<ItemTypeList>()
            .`in`(scope)
    }
}

private class ConfigTypeLoaderMapProvider @Inject constructor(
    private val objectLoader: ObjectTypeLoader,
    private val itemLoader: ItemTypeLoader
) : Provider<ConfigTypeLoaderMap> {

    override fun get(): ConfigTypeLoaderMap {
        return ConfigTypeLoaderMap().apply {
            register(ObjectType::class, objectLoader)
            register(ItemType::class, itemLoader)
        }
    }
}
