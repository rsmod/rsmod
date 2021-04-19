package org.rsmod.plugins.api.cache.name.ui

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.name.NamedTypeLoader
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import org.rsmod.plugins.api.cache.type.ui.InterfaceTypeList
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

class UserInterfaceNameLoader @Inject constructor(
    private val mapper: ObjectMapper,
    private val files: NamedConfigFileMap,
    private val names: UserInterfaceNameMap,
    private val types: InterfaceTypeList
) : NamedTypeLoader {

    override fun load(directory: Path) {
        val files = files.getValue(DefaultExtensions.INTERFACE_NAMES)
        files.forEach(::loadAliasFile)
        logger.info { "Loaded ${names.size} interface type name${if (names.size != 1) "s" else ""}" }
    }

    private fun loadAliasFile(file: Path): Int {
        var count = 0
        Files.newInputStream(file).use { input ->
            val nodes = mapper.readValue(input, LinkedHashMap<String, Int>()::class.java)
            nodes.forEach { node ->
                val name = node.key
                val interfaceId = node.value
                val type = types.getOrNull(interfaceId) ?: error(
                    "Interface type does not exist " +
                        "(interface=$interfaceId, file=${file.fileName}, path=${file.toAbsolutePath()})"
                )
                names[name] = type
                count++
            }
        }
        return count
    }
}
