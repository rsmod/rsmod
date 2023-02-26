package org.rsmod.plugins.api.config.packer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.openrs2.cache.Cache
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cache.name.CacheTypeNameLoader
import org.rsmod.plugins.api.cache.type.enums.EnumType
import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.cache.type.enums.EnumTypePacker
import org.rsmod.plugins.api.cache.type.varbit.VarbitType
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypePacker
import org.rsmod.plugins.api.cache.type.varp.VarpType
import org.rsmod.plugins.api.cache.type.varp.VarpTypeList
import org.rsmod.plugins.api.cache.type.varp.VarpTypePacker
import org.rsmod.plugins.api.config.type.ConfigEnum
import org.rsmod.plugins.api.config.type.ConfigVarbit
import org.rsmod.plugins.api.config.type.ConfigVarp
import org.rsmod.plugins.api.pluginPath
import org.rsmod.plugins.types.NamedEnum
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp
import org.rsmod.toml.Toml
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.walk

private val logger = InlineLogger()

@OptIn(ExperimentalPathApi::class)
public class ConfigCachePacker @Inject constructor(
    @Toml private val mapper: ObjectMapper,
    private val config: GameConfig,
    private val varps: VarpTypeList,
    private val varbits: VarbitTypeList,
    private val enums: EnumTypeList,
    nameLoader: CacheTypeNameLoader
) {

    private val names by lazy { nameLoader.load() }

    public fun pack(cache: Cache, isJs5: Boolean) {
        val mapped = config.pluginPath.configFiles()
        run packEnums@{
            val files = mapped[ConfigType.Enum] ?: emptyList()
            val types = packEnums(cache, files, isJs5)
            logger.info {
                "Packed ${types.size} enum${if (types.size != 1) "s" else ""} " +
                    "to ${if (isJs5) "js5" else "game"} cache."
            }
        }
        run packVarps@{
            val files = mapped[ConfigType.Varp] ?: emptyList()
            val types = packVarps(cache, files, isJs5)
            logger.info {
                "Packed ${types.size} varp${if (types.size != 1) "s" else ""} " +
                    "to ${if (isJs5) "js5" else "game"} cache."
            }
        }
        run packVarbits@{
            val files = mapped[ConfigType.Varbit] ?: emptyList()
            val types = packVarbits(cache, files, isJs5)
            logger.info {
                "Packed ${types.size} varbit${if (types.size != 1) "s" else ""} " +
                    "to ${if (isJs5) "js5" else "game"} cache."
            }
        }
    }

    private fun packVarps(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<VarpType> {
        val types = mutableListOf<VarpType>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigVarp>(input, VARP_TYPE_KEY)
                types += configs.map { it.toCacheType(names, varps) }
                names.varps.putAll(configs.map { it.name to NamedVarp(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarpTypePacker.pack(cache, types, isJs5)
    }

    private fun packVarbits(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigVarbit>(input, VARBIT_TYPE_KEY)
                types += configs.map { it.toCacheType(names, varbits) }
                names.varbits.putAll(configs.map { it.name to NamedVarbit(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarbitTypePacker.pack(cache, types, isJs5)
    }

    private fun packEnums(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<EnumType<Any, Any>> {
        val types = mutableListOf<EnumType<Any, Any>>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigEnum>(input, ENUM_TYPE_KEY)
                types += configs.map { it.toCacheType(names, enums) }
                names.enums.putAll(configs.map { it.name to NamedEnum(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return EnumTypePacker.pack(cache, types, isJs5)
    }

    private inline fun <reified T> extractValues(input: InputStream, key: String): List<T> {
        val map = mapper.readValue(input, object : TypeReference<Map<String, List<T>>>() {})
        return map[key] ?: error("Could not extract $key values from input.")
    }

    private companion object {

        private const val ENUM_TYPE_KEY = "enum"
        private const val ENUM_DIRECTORY_KEY = "enums"

        private const val VARP_TYPE_KEY = "varp"
        private const val VARP_DIRECTORY_KEY = "varps"

        private const val VARBIT_TYPE_KEY = "varbit"
        private const val VARBIT_DIRECTORY_KEY = "varbits"

        private fun Path.configFiles(): Map<ConfigType, Iterable<Path>> {
            val mapped = mutableMapOf<ConfigType, MutableList<Path>>()
            walk().forEach { path ->
                if (path.isDirectory()) return@forEach
                val type = when (path.parent.name) {
                    ENUM_DIRECTORY_KEY -> ConfigType.Enum
                    VARBIT_DIRECTORY_KEY -> ConfigType.Varbit
                    VARP_DIRECTORY_KEY -> ConfigType.Varp
                    else -> return@forEach
                }
                val list = mapped.computeIfAbsent(type) { mutableListOf() }
                list.add(path)
            }
            return mapped
        }

        private sealed class ConfigType {
            object Enum : ConfigType()
            object Varp : ConfigType()
            object Varbit : ConfigType()
        }
    }
}
