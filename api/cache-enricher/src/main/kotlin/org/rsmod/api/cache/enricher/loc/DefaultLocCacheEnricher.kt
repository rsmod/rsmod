package org.rsmod.api.cache.enricher.loc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.type.script.dsl.LocPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.api.utils.io.InputStreams
import org.rsmod.game.type.loc.UnpackedLocType

public class DefaultLocCacheEnricher
@Inject
constructor(@Toml private val mapper: ObjectMapper, private val nameMapping: NameMapping) :
    LocCacheEnricher {
    private val names: Map<String, Int>
        get() = nameMapping.locs

    override fun generate(): List<UnpackedLocType> {
        val external = loadExternalConfigs()
        return external.map { it.toCacheType() }
    }

    private fun loadExternalConfigs(): List<ExternalLocConfig> {
        val input = InputStreams.readAllBytes<DefaultLocCacheEnricher>("locs.toml")
        val type = object : TypeReference<Map<String, List<ExternalLocConfig>>>() {}
        val map = mapper.readValue(input, type)
        val configs =
            map[CONFIG_MAP_KEY] ?: error("Could not extract `${CONFIG_MAP_KEY}` value from input.")
        return configs
    }

    private fun ExternalLocConfig.toCacheType(): UnpackedLocType {
        val id = id ?: names[loc] ?: error("Mapping with name not found: $loc")
        val builder = LocPluginBuilder(loc ?: "loc_${this.id}")
        return builder.apply(this).build(id)
    }

    private fun LocPluginBuilder.apply(config: ExternalLocConfig): LocPluginBuilder {
        desc = config.examine
        return this
    }

    private companion object {
        const val CONFIG_MAP_KEY: String = "config"
    }
}

private data class ExternalLocConfig(val loc: String?, val id: Int?, val examine: String)
