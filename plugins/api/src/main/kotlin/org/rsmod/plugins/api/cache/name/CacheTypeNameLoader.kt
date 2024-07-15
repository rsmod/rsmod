package org.rsmod.plugins.api.cache.name

import org.rsmod.plugins.cache.config.enums.EnumType
import org.rsmod.plugins.cache.config.enums.EnumTypeList
import org.rsmod.plugins.cache.config.item.ItemType
import org.rsmod.plugins.cache.config.item.ItemTypeList
import org.rsmod.plugins.cache.config.npc.NpcType
import org.rsmod.plugins.cache.config.npc.NpcTypeList
import org.rsmod.plugins.cache.config.obj.ObjectType
import org.rsmod.plugins.cache.config.obj.ObjectTypeList
import org.rsmod.plugins.cache.config.param.ParamType
import org.rsmod.plugins.cache.config.param.ParamTypeList
import org.rsmod.plugins.cache.config.varbit.VarbitType
import org.rsmod.plugins.cache.config.varbit.VarbitTypeList
import org.rsmod.plugins.cache.config.varp.VarpType
import org.rsmod.plugins.cache.config.varp.VarpTypeList
import org.rsmod.plugins.types.NamedEnum
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedObject
import org.rsmod.plugins.types.NamedParameter
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp
import jakarta.inject.Inject

public class CacheTypeNameLoader @Inject constructor(
    private val items: ItemTypeList,
    private val npcs: NpcTypeList,
    private val objs: ObjectTypeList,
    private val varps: VarpTypeList,
    private val varbits: VarbitTypeList,
    private val enums: EnumTypeList,
    private val params: ParamTypeList
) {

    public fun load(): NamedTypeMapHolder {
        val names = NamedTypeMapHolder()
        names.putItems(items.values)
        names.putNpcs(npcs.values)
        names.putObjs(objs.values)
        names.putVarps(varps.values)
        names.putVarbits(varbits.values)
        names.putEnums(enums.values)
        names.putParams(params.values)
        return names
    }

    private fun NamedTypeMapHolder.putItems(types: Iterable<ItemType>) {
        types.forEach {
            it.internalName?.let { internal -> items[internal] = NamedItem(it.id) }
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
            val name = it.name ?: return@forEach
            varps[name] = NamedVarp(it.id)
        }
    }

    private fun NamedTypeMapHolder.putVarbits(types: Iterable<VarbitType>) {
        types.forEach {
            val name = it.name ?: return@forEach
            varbits[name] = NamedVarbit(it.id)
        }
    }

    private fun NamedTypeMapHolder.putEnums(types: Iterable<EnumType<Any, Any>>) {
        types.forEach {
            val name = it.name ?: return@forEach
            enums[name] = NamedEnum(it.id)
        }
    }

    private fun NamedTypeMapHolder.putParams(types: Iterable<ParamType<*>>) {
        types.forEach {
            val name = it.name ?: return@forEach
            parameters[name] = NamedParameter(it.id)
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
