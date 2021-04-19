package org.rsmod.plugins.api.cache.name.vars

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.vars.type.VarbitTypeList
import org.rsmod.game.name.NamedTypeLoader
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

class VarbitNameLoader @Inject constructor(
    private val mapper: ObjectMapper,
    private val files: NamedConfigFileMap,
    private val names: VarbitNamedMap,
    private val types: VarbitTypeList
) : NamedTypeLoader {

    override fun load(directory: Path) {
        val files = files.getValue(DefaultExtensions.VARBIT_NAMES)
        files.forEach(::loadAliasFile)
        logger.info { "Loaded ${names.size} varbit type name${if (names.size != 1) "s" else ""}" }
    }

    private fun loadAliasFile(file: Path) {
        Files.newInputStream(file).use { input ->
            val nodes = mapper.readValue(input, LinkedHashMap<String, Int>()::class.java)
            nodes.forEach { node ->
                val name = node.key
                val id = node.value
                val type = types.getOrNull(id) ?: error(
                    "Varbit type does not exist (varbit=$id, file=${file.fileName}, path=${file.toAbsolutePath()})"
                )
                names[name] = type
            }
        }
    }
}
