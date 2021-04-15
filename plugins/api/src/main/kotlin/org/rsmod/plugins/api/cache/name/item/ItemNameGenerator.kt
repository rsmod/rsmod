package org.rsmod.plugins.api.cache.name.item

import com.fasterxml.jackson.databind.ObjectMapper
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.plugins.api.cache.normalizeForNamedMap
import org.rsmod.plugins.api.cache.stripTags
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

class ItemNameGenerator @Inject constructor(
    private val mapper: ObjectMapper,
    private val types: ItemTypeList
) {

    fun generate(path: Path) {
        val names = types.toNameMap()
        Files.newBufferedWriter(path).use { writer ->
            mapper.writeValue(writer, names)
        }
    }

    private fun ItemTypeList.toNameMap(): Map<String, Int> {
        val names = mutableMapOf<String, Int>()
        forEach {
            val name = it.internalName()
            names[name] = it.id
        }
        return names
    }

    private fun ItemType.internalName(): String {
        val normalized = name.normalizeForNamedMap()
        val name = normalized.toLowerCase().replace(" ", "_")
        return name.stripTags() + "_" + id
    }
}
