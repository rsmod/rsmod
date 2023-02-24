package org.rsmod.plugins.api.cache.name

import org.rsmod.plugins.api.cache.type.item.ItemType
import org.rsmod.plugins.api.cache.type.item.ItemTypeLoader
import org.rsmod.plugins.api.cache.type.npc.NpcType
import org.rsmod.plugins.api.cache.type.npc.NpcTypeLoader
import org.rsmod.plugins.api.cache.type.obj.ObjectType
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeLoader
import org.rsmod.plugins.api.cache.type.varbit.VarbitType
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeLoader
import org.rsmod.plugins.api.cache.type.varp.VarpType
import org.rsmod.plugins.api.cache.type.varp.VarpTypeLoader
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedObject
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp
import javax.inject.Inject

public class CacheTypeNameLoader @Inject constructor(
    private val items: ItemTypeLoader,
    private val npcs: NpcTypeLoader,
    private val objs: ObjectTypeLoader,
    private val varps: VarpTypeLoader,
    private val varbits: VarbitTypeLoader
) {

    public fun load(): NamedTypeMapHolder {
        val names = NamedTypeMapHolder()
        names.putItems(items.load())
        names.putNpcs(npcs.load())
        names.putObjs(objs.load())
        names.putVarps(varps.load())
        names.putVarbits(varbits.load())
        return names
    }

    private fun NamedTypeMapHolder.putItems(types: List<ItemType>) {
        types.forEach {
            if (it.isPlaceholder || it.isNoted) return@forEach
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = sanitized + "_${it.id}"
            items[name] = NamedItem(it.id)
        }
    }

    private fun NamedTypeMapHolder.putNpcs(types: List<NpcType>) {
        types.forEach {
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = sanitized + "_${it.id}"
            npcs[name] = NamedNpc(it.id)
        }
    }

    private fun NamedTypeMapHolder.putObjs(types: List<ObjectType>) {
        types.forEach {
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = sanitized + "_${it.id}"
            objs[name] = NamedObject(it.id)
        }
    }

    private fun NamedTypeMapHolder.putVarps(types: List<VarpType>) {
        types.forEach {
            val name = it.alias ?: return@forEach
            varps[name] = NamedVarp(it.id)
        }
    }

    private fun NamedTypeMapHolder.putVarbits(types: List<VarbitType>) {
        types.forEach {
            val name = it.alias ?: return@forEach
            varbits[name] = NamedVarbit(it.id)
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
