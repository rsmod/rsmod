package org.rsmod.plugins.api.config.packer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Key
import org.openrs2.cache.Cache
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.build.js5.Js5Cache
import org.rsmod.plugins.api.cache.type.varbit.VarbitType
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeBuilder
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeLoader
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypePacker
import org.rsmod.plugins.api.cache.type.varp.VarpType
import org.rsmod.plugins.api.cache.type.varp.VarpTypeBuilder
import org.rsmod.plugins.api.cache.type.varp.VarpTypeLoader
import org.rsmod.plugins.api.cache.type.varp.VarpTypePacker
import org.rsmod.plugins.api.config.type.PluginVarbit
import org.rsmod.plugins.api.config.type.PluginVarp
import org.rsmod.plugins.api.pluginPath
import org.rsmod.plugins.types.NamedTypeMapHolder
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

public fun main(args: Array<String>) {
    val guice = Guice.createInjector(PluginConfigPackerModule)
    val gameCache = guice.getInstance(Key.get(Cache::class.java, GameCache::class.java))
    val js5Cache = guice.getInstance(Key.get(Cache::class.java, Js5Cache::class.java))
    val packer = guice.getInstance(PluginConfigCachePacker::class.java)
    gameCache.use { packer.pack(isJs5 = false, it) }
    println()
    js5Cache.use { packer.pack(isJs5 = true, it) }
}

public class PluginConfigCachePacker @Inject constructor(
    @Toml private val mapper: ObjectMapper,
    @GameCache private val names: NamedTypeMapHolder,
    private val config: GameConfig,
    private val varpLoader: VarpTypeLoader,
    private val varbitLoader: VarbitTypeLoader
) {

    private val varpTypes by lazy { varpLoader.load().associateBy { it.id } }
    private val varbitTypes by lazy { varbitLoader.load().associateBy { it.id } }

    @OptIn(ExperimentalPathApi::class)
    public fun pack(isJs5: Boolean, cache: Cache) {
        // TODO: inform of possible id collisions
        val pluginFiles = config.pluginPath.walk()
        run packVarps@{
            val files = pluginFiles.filter { it.nameWithoutExtension.endsWith("varps") }
            val types = packVarps(isJs5, cache, files)
            logger.info { "Packed ${types.size} plugin varps to ${if (isJs5) "js5" else "game"} cache." }
        }
        run packVarbits@{
            val files = pluginFiles.filter { it.nameWithoutExtension.endsWith("varbits") }
            val types = packVarbits(isJs5, cache, files)
            logger.info { "Packed ${types.size} plugin varbits to ${if (isJs5) "js5" else "game"} cache." }
        }
    }

    private fun packVarps(isJs5: Boolean, cache: Cache, files: Sequence<Path>): List<VarpType> {
        val types = mutableListOf<VarpType>()
        files.forEach { f ->
            Files.newInputStream(f).use { input ->
                val pluginTypes = extract<PluginVarp>(input, VARPS_KEY)
                    ?: error("Could not extract varps from $f")
                types += pluginTypes.map { it.toCacheType() }
                names.varps.putAll(pluginTypes.map { it.alias to NamedVarp(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarpTypePacker.pack(isJs5, cache, types)
    }

    private fun packVarbits(isJs5: Boolean, cache: Cache, files: Sequence<Path>): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        files.forEach { f ->
            Files.newInputStream(f).use { input ->
                val pluginTypes = extract<PluginVarbit>(input, VARBITS_KEY)
                    ?: error("Could not extract varbits from $f")
                types += pluginTypes.map { it.toCacheType() }
                names.varbits.putAll(pluginTypes.map { it.alias to NamedVarbit(it.id) })
            }
        }
        if (isJs5) types.removeIf { !it.transmit }
        return VarbitTypePacker.pack(isJs5, cache, types)
    }

    private fun PluginVarp.toCacheType(): VarpType {
        val builder = VarpTypeBuilder()
        builder.id = id
        builder.alias = alias
        builder.clientCode = clientCode
        builder.transmit = transmit
        inherit?.let {
            // TODO: informative error if varp type does not exist
            val named = names.varps.getValue(it.stripTag())
            builder += varpTypes.getValue(named.id)
        }
        return builder.build()
    }

    private fun PluginVarbit.toCacheType(): VarbitType {
        val builder = VarbitTypeBuilder()
        // TODO: informative error if varp type does not exist
        val varp = names.varps.getValue(varp.stripTag())
        builder.id = id
        builder.alias = alias
        builder.varp = varp.id
        builder.lsb = lsb
        builder.msb = msb
        builder.transmit = transmit
        inherit?.let {
            // TODO: informative error if varbit type does not exist
            val named = names.varbits.getValue(it.stripTag())
            builder += varbitTypes.getValue(named.id)
        }
        return builder.build()
    }

    private inline fun <reified T> extract(input: InputStream, key: String): List<T>? {
        val map = mapper.readValue(input, object : TypeReference<Map<String, List<T>>>() {})
        return map[key]
    }

    private fun String.stripTag(): String {
        if (indexOf('.') == -1) return this
        return substring(indexOf('.') + 1, length)
    }

    private companion object {

        private const val VARPS_KEY = "varp"
        private const val VARBITS_KEY = "varbit"
    }
}
