package org.rsmod.plugins.api.cache.name

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Scopes
import dev.misfitlabs.kotlinguice4.getInstance
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.CacheTypeLoaderList
import org.rsmod.game.config.ConfigModule
import org.rsmod.plugins.api.cache.name.item.ItemNameGenerator
import org.rsmod.plugins.api.cache.name.item.ItemNameLoader
import org.rsmod.plugins.api.cache.name.npc.NpcNameGenerator
import org.rsmod.plugins.api.cache.name.npc.NpcNameLoader
import org.rsmod.plugins.api.cache.name.obj.ObjectNameGenerator
import org.rsmod.plugins.api.cache.name.obj.ObjectNameLoader
import org.rsmod.plugins.api.cache.type.TypeLoaderModule
import org.rsmod.util.mapper.ObjectMapperModule
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

private const val NAME_DIRECTORY = "name"

class NameGenerator @Inject constructor(
    private val npcGenerator: NpcNameGenerator,
    private val itemGenerator: ItemNameGenerator,
    private val objGenerator: ObjectNameGenerator
) {

    fun generate(basePath: Path) {
        npcGenerator.generate(basePath.resolve(NpcNameLoader.FILE_NAME))
        itemGenerator.generate(basePath.resolve(ItemNameLoader.FILE_NAME))
        objGenerator.generate(basePath.resolve(ObjectNameLoader.FILE_NAME))
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val scope = Scopes.SINGLETON
            val modules = arrayOf(
                ObjectMapperModule(scope),
                ConfigModule(scope),
                CacheModule(scope),
                TypeLoaderModule(scope)
            )
            val injector = Guice.createInjector(*modules)

            val loaders: CacheTypeLoaderList = injector.getInstance()
            loaders.forEach { it.load() }

            val cache: GameCache = injector.getInstance()
            val basePath = cache.directory.parent.resolve(NAME_DIRECTORY)
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath)
                logger.info { "Created generated path: ${basePath.toAbsolutePath()}" }
            }

            val generator: NameGenerator = injector.getInstance()
            generator.generate(basePath)
        }
    }
}
