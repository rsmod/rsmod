package org.rsmod.plugins.cache.packer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.openrs2.cache.Cache
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cache.name.CacheTypeNameLoader
import org.rsmod.plugins.api.pluginConfigPath
import org.rsmod.plugins.cache.config.enums.EnumType
import org.rsmod.plugins.cache.config.enums.EnumTypeList
import org.rsmod.plugins.cache.config.enums.EnumTypePacker
import org.rsmod.plugins.cache.config.item.ItemType
import org.rsmod.plugins.cache.config.item.ItemTypeList
import org.rsmod.plugins.cache.config.item.ItemTypePacker
import org.rsmod.plugins.cache.config.param.ParamType
import org.rsmod.plugins.cache.config.param.ParamTypeList
import org.rsmod.plugins.cache.config.param.ParamTypePacker
import org.rsmod.plugins.cache.config.varbit.VarbitType
import org.rsmod.plugins.cache.config.varbit.VarbitTypeList
import org.rsmod.plugins.cache.config.varbit.VarbitTypePacker
import org.rsmod.plugins.cache.config.varp.VarpType
import org.rsmod.plugins.cache.config.varp.VarpTypeList
import org.rsmod.plugins.cache.config.varp.VarpTypePacker
import org.rsmod.plugins.cache.packer.type.ConfigEnum
import org.rsmod.plugins.cache.packer.type.ConfigItem
import org.rsmod.plugins.cache.packer.type.ConfigParam
import org.rsmod.plugins.cache.packer.type.ConfigVarbit
import org.rsmod.plugins.cache.packer.type.ConfigVarp
import org.rsmod.plugins.types.NamedEnum
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedParameter
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
    private val params: ParamTypeList,
    private val items: ItemTypeList,
    nameLoader: CacheTypeNameLoader
) {

    private val names by lazy { nameLoader.load() }

    // This is only required to support the same injection
    // of this class being used to pack multiple cache builds.
    // The alternative is to use one injector per cache build.
    private val updatedParams = mutableMapOf<Int, ParamType<*>>()

    public fun pack(cache: Cache, isJs5: Boolean) {
        val mapped = config.pluginConfigPath.configFiles()
        run packParams@{
            val files = mapped[ConfigType.Param] ?: emptyList()
            val types = packParams(cache, files, isJs5)
            updatedParams += types.associateBy { it.id }
            logger.info {
                "Packed ${types.size} param${if (types.size != 1) "s" else ""} " +
                    "to ${if (isJs5) "js5" else "game"} cache."
            }
        }
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
        run packItems@{
            val files = mapped[ConfigType.Item] ?: emptyList()
            val updatedParams = ParamTypeList(params + updatedParams)
            val types = packItems(cache, updatedParams, files, isJs5)
            logger.info {
                "Packed ${types.size} item${if (types.size != 1) "s" else ""} " +
                    "to ${if (isJs5) "js5" else "game"} cache."
            }
        }
    }

    private fun packVarps(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<VarpType> {
        val types = mutableListOf<VarpType>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigVarp>(input, ConfigType.Varp.typeKey)
                types += configs.map { it.toCacheType(names, varps) }
                names.varps += configs.map { it.name to NamedVarp(it.id) }
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarpTypePacker.pack(cache, types, isJs5)
    }

    private fun packVarbits(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigVarbit>(input, ConfigType.Varbit.typeKey)
                types += configs.map { it.toCacheType(names, varbits) }
                names.varbits += configs.map { it.name to NamedVarbit(it.id) }
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarbitTypePacker.pack(cache, types, isJs5)
    }

    private fun packEnums(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<EnumType<Any, Any>> {
        val types = mutableListOf<EnumType<Any, Any>>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigEnum>(input, ConfigType.Enum.typeKey)
                types += configs.map { it.toCacheType(names, enums) }
                names.enums += configs.map { it.name to NamedEnum(it.id) }
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return EnumTypePacker.pack(cache, types, isJs5)
    }

    private fun packParams(cache: Cache, files: Iterable<Path>, isJs5: Boolean): List<ParamType<*>> {
        val types = mutableListOf<ParamType<*>>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigParam>(input, ConfigType.Param.typeKey)
                types += configs.map { it.toCacheType(names, params) }
                names.parameters += configs.map { it.name to NamedParameter(it.id) }
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return ParamTypePacker.pack(cache, types, isJs5)
    }

    private fun packItems(
        cache: Cache,
        params: ParamTypeList,
        files: Iterable<Path>,
        isJs5: Boolean
    ): List<ItemType> {
        val types = mutableListOf<ItemType>()
        files.forEach { file ->
            Files.newInputStream(file).use { input ->
                val configs = extractValues<ConfigItem>(input, ConfigType.Item.typeKey)
                types += configs.map { it.toCacheType(names, items, params) }
                names.items += configs.map { it.name to NamedItem(it.id) }
                names.items += configs.filter { it.internalName != null }
                    .map { it.name to NamedItem(it.id) }
            }
        }
        return ItemTypePacker.pack(cache, types, params, isJs5)
    }

    private inline fun <reified T> extractValues(input: InputStream, key: String): List<T> {
        val map = mapper.readValue(input, object : TypeReference<Map<String, List<T>>>() {})
        return map[key] ?: error("Could not extract $key values from input.")
    }

    private companion object {

        private fun Path.configFiles(): Map<ConfigType, Iterable<Path>> {
            val mapped = mutableMapOf<ConfigType, MutableList<Path>>()
            walk().forEach { path ->
                if (path.isDirectory()) return@forEach
                val type = ConfigType.directoryMapped[path.parent.name] ?: return@forEach
                val list = mapped.computeIfAbsent(type) { mutableListOf() }
                list.add(path)
            }
            return mapped
        }

        private enum class ConfigType(val directoryKey: String, val typeKey: String) {
            Enum(directoryKey = "enums", typeKey = "enum"),
            Item(directoryKey = "items", typeKey = "item"),
            Param(directoryKey = "params", typeKey = "param"),
            Varp(directoryKey = "varps", typeKey = "varp"),
            Varbit(directoryKey = "varbits", typeKey = "varbit");

            companion object {

                val values = enumValues<ConfigType>()

                val directoryMapped = values.associateBy { it.directoryKey }
            }
        }
    }
}
