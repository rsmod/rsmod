package org.rsmod.plugins.api.config.packer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.openrs2.cache.Cache
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cache.name.CacheTypeNameLoader
import org.rsmod.plugins.api.cache.type.varbit.VarbitType
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypePacker
import org.rsmod.plugins.api.cache.type.varp.VarpType
import org.rsmod.plugins.api.cache.type.varp.VarpTypeList
import org.rsmod.plugins.api.cache.type.varp.VarpTypePacker
import org.rsmod.plugins.api.config.type.ConfigVarbit
import org.rsmod.plugins.api.config.type.ConfigVarp
import org.rsmod.plugins.api.pluginPath
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp
import org.rsmod.toml.Toml
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.walk

private val logger = InlineLogger()

public class ConfigCachePacker @Inject constructor(
    @Toml private val mapper: ObjectMapper,
    private val config: GameConfig,
    private val varps: VarpTypeList,
    private val varbits: VarbitTypeList,
    nameLoader: CacheTypeNameLoader
) {

    private val names by lazy { nameLoader.load() }

    @OptIn(ExperimentalPathApi::class)
    public fun pack(cache: Cache, isJs5: Boolean) {
        val pluginFiles = config.pluginPath.walk()
        run packVarps@{
            val files = pluginFiles.filter { it.nameWithoutExtension.endsWith(VARP_FILE_KEY) }
            val types = packVarps(cache, files, isJs5)
            logger.info { "Packed ${types.size} plugin varps to ${if (isJs5) "js5" else "game"} cache." }
        }
        run packVarbits@{
            val files = pluginFiles.filter { it.nameWithoutExtension.endsWith(VARBIT_FILE_KEY) }
            val types = packVarbits(cache, files, isJs5)
            logger.info { "Packed ${types.size} plugin varbits to ${if (isJs5) "js5" else "game"} cache." }
        }
    }

    private fun packVarps(cache: Cache, files: Sequence<Path>, isJs5: Boolean): List<VarpType> {
        val types = mutableListOf<VarpType>()
        files.forEach { f ->
            Files.newInputStream(f).use { input ->
                val configs = extractValues<ConfigVarp>(input, VARP_TYPE_KEY)
                types += configs.map { it.toCacheType(names, varps) }
                names.varps.putAll(configs.map { it.alias to NamedVarp(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarpTypePacker.pack(cache, types, isJs5)
    }

    private fun packVarbits(cache: Cache, files: Sequence<Path>, isJs5: Boolean): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        files.forEach { f ->
            Files.newInputStream(f).use { input ->
                val configs = extractValues<ConfigVarbit>(input, VARBIT_TYPE_KEY)
                types += configs.map { it.toCacheType(names, varbits) }
                names.varbits.putAll(configs.map { it.alias to NamedVarbit(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarbitTypePacker.pack(cache, types, isJs5)
    }

    private inline fun <reified T> extractValues(input: InputStream, key: String): List<T> {
        val map = mapper.readValue(input, object : TypeReference<Map<String, List<T>>>() {})
        return map[key] ?: error("Could not extract $key values from input.")
    }

    private companion object {

        private const val VARP_TYPE_KEY = "varp"
        private const val VARP_FILE_KEY = "varps"

        private const val VARBIT_TYPE_KEY = "varbit"
        private const val VARBIT_FILE_KEY = "varbits"
    }
}
