package gg.rsmod.plugins.api.cache.config

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.cache.ConfigTypeLoaderList
import gg.rsmod.game.model.item.ItemTypeList
import gg.rsmod.game.model.obj.ObjectTypeList
import gg.rsmod.plugins.api.cache.config.item.ItemTypeLoader
import gg.rsmod.plugins.api.cache.config.obj.ObjectTypeLoader

class TypeLoaderModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<ConfigTypeLoaderList>()
            .toProvider<ConfigTypeLoaderListProvider>()
            .`in`(scope)

        bind<ObjectTypeList>()
            .`in`(scope)

        bind<ItemTypeList>()
            .`in`(scope)
    }
}

private class ConfigTypeLoaderListProvider @Inject constructor(
    private val objectLoader: ObjectTypeLoader,
    private val itemLoader: ItemTypeLoader
) : Provider<ConfigTypeLoaderList> {

    override fun get(): ConfigTypeLoaderList {
        return ConfigTypeLoaderList().apply {
            register(objectLoader)
            register(itemLoader)
        }
    }
}
