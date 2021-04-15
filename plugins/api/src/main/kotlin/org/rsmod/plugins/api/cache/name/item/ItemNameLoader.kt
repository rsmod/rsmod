package org.rsmod.plugins.api.cache.name.item

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.name.NamedTypeLoader
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

class ItemNameLoader @Inject constructor(
    private val mapper: ObjectMapper,
    private val files: NamedConfigFileMap,
    private val names: ItemNameMap,
    private val types: ItemTypeList
) : NamedTypeLoader {

    override fun load(directory: Path) {
        val defaultNameFile = directory.resolve(FILE_NAME)
        if (Files.exists(defaultNameFile)) {
            loadNameFile(defaultNameFile)
        }
        val initialSize = names.size
        val files = files.getValue(DefaultExtensions.ITEM_NAMES)
        val aliasSize = files.sumBy { loadAliasFile(it) }
        logger.info { "Loaded $initialSize item type names ($aliasSize ${if (aliasSize != 1) "aliases" else "alias"})" }
    }

    private fun loadNameFile(file: Path) {
        Files.newInputStream(file).use { input ->
            val nodes = mapper.readValue(input, LinkedHashMap<String, Int>()::class.java)
            nodes.forEach { node ->
                val key = node.key
                val value = node.value
                val type = types[value]
                names[key] = type
            }
        }
    }

    private fun loadAliasFile(file: Path): Int {
        var count = 0
        Files.newInputStream(file).use { input ->
            val nodes = mapper.readValue(input, LinkedHashMap<String, String>()::class.java)
            nodes.forEach { node ->
                val key = node.key
                val value = node.value
                val type = names[value] ?: error(
                    "Type with name does not exist (name=$value, file=${file.fileName}, path=${file.toAbsolutePath()})"
                )
                names[key] = type
                count++
            }
        }
        return count
    }

    companion object {

        const val FILE_NAME = "items.yml"
    }
}
