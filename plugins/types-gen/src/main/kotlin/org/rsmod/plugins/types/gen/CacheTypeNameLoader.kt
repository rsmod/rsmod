package org.rsmod.plugins.types.gen

import org.rsmod.game.types.NamedItem
import org.rsmod.game.types.NamedNpc
import org.rsmod.game.types.NamedObject
import org.rsmod.game.types.NamedTypeMapHolder
import org.rsmod.plugins.api.cache.type.item.ItemType
import org.rsmod.plugins.api.cache.type.item.ItemTypeLoader
import org.rsmod.plugins.api.cache.type.npc.NpcType
import org.rsmod.plugins.api.cache.type.npc.NpcTypeLoader
import org.rsmod.plugins.api.cache.type.obj.ObjectType
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeLoader
import javax.inject.Inject

class CacheTypeNameLoader @Inject constructor(
    private val items: ItemTypeLoader,
    private val npcs: NpcTypeLoader,
    private val objs: ObjectTypeLoader
) {

    fun loadAndPutAll(dest: NamedTypeMapHolder) {
        dest.putItems(items.load())
        dest.putNpcs(npcs.load())
        dest.putObjs(objs.load())
    }

    private fun NamedTypeMapHolder.putItems(types: List<ItemType>) {
        types.forEach {
            if (it.isPlaceholder || it.isNoted) return@forEach
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = if (items.containsKey(sanitized)) sanitized + "_${it.id}" else sanitized
            items[name] = NamedItem(it.id)
        }
    }

    private fun NamedTypeMapHolder.putNpcs(types: List<NpcType>) {
        types.forEach {
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = if (npcs.containsKey(sanitized)) sanitized + "_${it.id}" else sanitized
            npcs[name] = NamedNpc(it.id)
        }
    }

    private fun NamedTypeMapHolder.putObjs(types: List<ObjectType>) {
        types.forEach {
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = if (objs.containsKey(sanitized)) sanitized + "_${it.id}" else sanitized
            objs[name] = NamedObject(it.id)
        }
    }

    private fun String.sanitize(): String? {
        val name = lowercase()
        val sanitized = when {
            name.isBlank() || name == "null" -> return null
            name == "package" -> "_package"
            Character.isDigit(name[0]) -> "_$name"
            else -> name
        }
        return sanitized
            .replace(' ', '_')
            .replace('-', '_')
            .replace("[^a-zA-Z0-9_]".toRegex(), "")
    }
}
