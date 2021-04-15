package org.rsmod.plugins.api.cache.name.npc

import com.fasterxml.jackson.databind.ObjectMapper
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.plugins.api.cache.normalizeForNamedMap
import org.rsmod.plugins.api.cache.stripTags
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

class NpcNameGenerator @Inject constructor(
    private val mapper: ObjectMapper,
    private val types: NpcTypeList
) {

    fun generate(path: Path) {
        val names = types.toNameMap()
        Files.newBufferedWriter(path).use { writer ->
            mapper.writeValue(writer, names)
        }
    }

    private fun NpcTypeList.toNameMap(): Map<String, Int> {
        val names = mutableMapOf<String, Int>()
        forEach {
            val name = it.internalName()
            names[name] = it.id
        }
        return names
    }

    private fun NpcType.internalName(): String {
        val normalized = name.normalizeForNamedMap()
        val name = normalized.toLowerCase().replace(" ", "_")
        return name.stripTags() + "_" + id
    }
}
