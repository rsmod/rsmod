package org.rsmod.plugins.api.cache.type

import javax.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.cache.type.ConfigTypeLoaderList
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.game.model.vars.type.VarbitTypeList
import org.rsmod.game.model.vars.type.VarpTypeList
import org.rsmod.plugins.api.cache.type.item.ItemTypeLoader
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeLoader
import org.rsmod.plugins.api.cache.type.vars.VarbitTypeLoader
import org.rsmod.plugins.api.cache.type.vars.VarpTypeLoader

class TypeLoaderModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<ConfigTypeLoaderList>()
            .toProvider<ConfigTypeLoaderListProvider>()
            .`in`(scope)
        bind<ObjectTypeList>().`in`(scope)
        bind<ItemTypeList>().`in`(scope)
        bind<VarpTypeList>().`in`(scope)
        bind<VarbitTypeList>().`in`(scope)
    }
}

private class ConfigTypeLoaderListProvider @Inject constructor(
    private val objectLoader: ObjectTypeLoader,
    private val itemLoader: ItemTypeLoader,
    private val varpLoader: VarpTypeLoader,
    private val varbitLoader: VarbitTypeLoader
) : Provider<ConfigTypeLoaderList> {

    override fun get(): ConfigTypeLoaderList {
        return ConfigTypeLoaderList().apply {
            register(objectLoader)
            register(itemLoader)
            register(varpLoader)
            register(varbitLoader)
        }
    }
}
