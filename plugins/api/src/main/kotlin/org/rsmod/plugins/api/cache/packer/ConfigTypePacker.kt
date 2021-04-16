package org.rsmod.plugins.api.cache.packer

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Scopes
import dev.misfitlabs.kotlinguice4.getInstance
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.cache.type.ConfigTypeLoaderList
import org.rsmod.game.config.ConfigModule
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileModule
import org.rsmod.plugins.api.cache.packer.item.ItemTypePacker
import org.rsmod.plugins.api.cache.packer.npc.NpcTypePacker
import org.rsmod.plugins.api.cache.packer.obj.ObjectTypePacker
import org.rsmod.plugins.api.cache.type.TypeLoaderModule
import org.rsmod.util.mapper.ObjectMapperModule

class ConfigTypePacker @Inject constructor(
    private val itemPacker: ItemTypePacker,
    private val objPacker: ObjectTypePacker,
    private val npcPacker: NpcTypePacker
) {

    fun packAll() {
        packNpcs()
        packItems()
        packObjs()
    }

    fun packItems() = itemPacker.pack()

    fun packObjs() = objPacker.pack()

    fun packNpcs() = npcPacker.pack()

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val scope = Scopes.SINGLETON
            val modules = arrayOf(
                ObjectMapperModule(scope),
                ConfigModule(scope),
                NamedConfigFileModule(scope),
                CacheModule(scope),
                TypeLoaderModule(scope)
            )
            val injector = Guice.createInjector(*modules)

            val loaders: ConfigTypeLoaderList = injector.getInstance()
            loaders.forEach { it.load() }

            val packer: ConfigTypePacker = injector.getInstance()
            if (args.isEmpty()) {
                packer.packAll()
                return
            }
            if (args.contains("-item")) packer.packItems()
            if (args.contains("-obj")) packer.packObjs()
            if (args.contains("-npc")) packer.packNpcs()
        }
    }
}
