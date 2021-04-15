package org.rsmod.plugins.api.cache.name.obj

import com.fasterxml.jackson.databind.ObjectMapper
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.plugins.api.cache.normalizeForNamedMap
import org.rsmod.plugins.api.cache.stripTags
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

class ObjectNameGenerator @Inject constructor(
    private val mapper: ObjectMapper,
    private val types: ObjectTypeList
) {

    fun generate(path: Path) {
        val names = types.toNameMap()
        Files.newBufferedWriter(path).use { writer ->
            mapper.writeValue(writer, names)
        }
    }

    private fun ObjectTypeList.toNameMap(): Map<String, Int> {
        val names = mutableMapOf<String, Int>()
        forEach {
            val name = it.internalName()
            names[name] = it.id
        }
        return names
    }

    private fun ObjectType.internalName(): String {
        val normalized = name.normalizeForNamedMap()
        val name = normalized.toLowerCase().replace(" ", "_")
        return name.stripTags() + "_" + id
    }
}
