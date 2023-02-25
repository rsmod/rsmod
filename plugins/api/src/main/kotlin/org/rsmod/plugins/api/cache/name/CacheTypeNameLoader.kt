package org.rsmod.plugins.api.cache.name

import org.rsmod.plugins.api.cache.type.item.ItemType
import org.rsmod.plugins.api.cache.type.item.ItemTypeList
import org.rsmod.plugins.api.cache.type.npc.NpcType
import org.rsmod.plugins.api.cache.type.npc.NpcTypeList
import org.rsmod.plugins.api.cache.type.obj.ObjectType
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeList
import org.rsmod.plugins.api.cache.type.varbit.VarbitType
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.cache.type.varp.VarpType
import org.rsmod.plugins.api.cache.type.varp.VarpTypeList
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedObject
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp
import javax.inject.Inject

public class CacheTypeNameLoader @Inject constructor(
    private val items: ItemTypeList,
    private val npcs: NpcTypeList,
    private val objs: ObjectTypeList,
    private val varps: VarpTypeList,
    private val varbits: VarbitTypeList
) {

    public fun load(): NamedTypeMapHolder {
        val names = NamedTypeMapHolder()
        names.putItems(items.values)
        names.putNpcs(npcs.values)
        names.putObjs(objs.values)
        names.putVarps(varps.values)
        names.putVarbits(varbits.values)
        return names
    }

    private fun NamedTypeMapHolder.putItems(types: Iterable<ItemType>) {
        types.forEach {
            if (it.isPlaceholder || it.isNoted) return@forEach
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = sanitized + "_${it.id}"
            items[name] = NamedItem(it.id)
        }
    }

    private fun NamedTypeMapHolder.putNpcs(types: Iterable<NpcType>) {
        types.forEach {
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = sanitized + "_${it.id}"
            npcs[name] = NamedNpc(it.id)
        }
    }

    private fun NamedTypeMapHolder.putObjs(types: Iterable<ObjectType>) {
        types.forEach {
            val sanitized = it.name.sanitize() ?: return@forEach
            val name = sanitized + "_${it.id}"
            objs[name] = NamedObject(it.id)
        }
    }

    private fun NamedTypeMapHolder.putVarps(types: Iterable<VarpType>) {
        types.forEach {
            val name = it.alias ?: return@forEach
            varps[name] = NamedVarp(it.id)
        }
    }

    private fun NamedTypeMapHolder.putVarbits(types: Iterable<VarbitType>) {
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
