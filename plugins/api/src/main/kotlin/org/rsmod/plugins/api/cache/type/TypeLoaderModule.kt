package org.rsmod.plugins.api.cache.type

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.cache.ConfigTypeLoaderList
import org.rsmod.game.model.item.ItemTypeList
import org.rsmod.game.model.obj.ObjectTypeList
import org.rsmod.plugins.api.cache.type.item.ItemTypeLoader
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeLoader

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
