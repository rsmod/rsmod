package org.rsmod.plugins.api.config.type

import org.rsmod.plugins.api.cache.type.item.ItemTypeList
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.convert
import org.rsmod.plugins.api.config.StringUtil.stripTag
import org.rsmod.plugins.cache.config.item.ItemType
import org.rsmod.plugins.cache.config.item.ItemTypeBuilder
import org.rsmod.plugins.cache.config.param.ParamMap
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedTypeMapHolder

public data class ConfigItem(
    val id: Int,
    val internalName: String?,
    val inherit: String?,
    val name: String,
    val model: Int?,
    val zoom2d: Int?,
    val xan2d: Int?,
    val yan2d: Int?,
    val zan2d: Int?,
    val xOff2d: Int?,
    val yOff2d: Int?,
    val stacks: Boolean?,
    val cost: Int?,
    val members: Boolean?,
    val maleModelOffset: Int?,
    val femaleModelOffset: Int?,
    val maleModel0: Int?,
    val maleModel1: Int?,
    val maleModel2: Int?,
    val femaleModel0: Int?,
    val femaleModel1: Int?,
    val femaleModel2: Int?,
    val maleHeadModel0: Int?,
    val maleHeadModel1: Int?,
    val femaleHeadModel0: Int?,
    val femaleHeadModel1: Int?,
    val category: Int?,
    val groundOps: List<String?>?,
    val inventoryOps: List<String?>?,
    val recolorSrc: List<Int>?,
    val recolorDest: List<Int>?,
    val retextureSrc: List<Int>?,
    val retextureDest: List<Int>?,
    val dropOpIndex: Int?,
    val resizeX: Int?,
    val resizeY: Int?,
    val resizeZ: Int?,
    val ambient: Int?,
    val contrast: Int?,
    val exchangeable: Boolean?,
    val team: Int?,
    val noteLink: Any?,
    val noteModel: Any?,
    val placeholderLink: Any?,
    val placeholderModel: Any?,
    val boughtLink: Any?,
    val boughtValue: Any?,
    val countItem: List<Any>?,
    val countCo: List<Int>?,
    val params: Map<String, String>?,
    val weight: Int?,
    val wearPos1: Int?,
    val wearPos2: Int?,
    val wearPos3: Int?
) {

    public fun toCacheType(
        names: NamedTypeMapHolder,
        itemTypes: ItemTypeList,
        paramTypes: ParamTypeList
    ): ItemType {
        val builder = ItemTypeBuilder()
        builder.id = id
        builder.internalName = internalName
        builder.name = name
        model?.let { builder.model = it }
        zoom2d?.let { builder.zoom2d = it }
        xan2d?.let { builder.xan2d = it }
        yan2d?.let { builder.yan2d = it }
        zan2d?.let { builder.zan2d = it }
        xOff2d?.let { builder.xOff2d = it }
        yOff2d?.let { builder.yOff2d = it }
        stacks?.let { builder.stacks = it }
        cost?.let { builder.cost = it }
        members?.let { builder.members = it }
        maleModelOffset?.let { builder.maleModelOffset = it }
        femaleModelOffset?.let { builder.femaleModelOffset = it }
        maleModel0?.let { builder.maleModel0 = it }
        maleModel1?.let { builder.maleModel1 = it }
        maleModel2?.let { builder.maleModel2 = it }
        femaleModel0?.let { builder.femaleModel0 = it }
        femaleModel1?.let { builder.femaleModel1 = it }
        femaleModel2?.let { builder.femaleModel2 = it }
        maleHeadModel0?.let { builder.maleHeadModel0 = it }
        maleHeadModel1?.let { builder.maleHeadModel1 = it }
        femaleHeadModel0?.let { builder.femaleHeadModel0 = it }
        femaleHeadModel1?.let { builder.femaleHeadModel1 = it }
        // TODO: can't recall if items can have more than a single category
        category?.let { builder.categories = setOf(it) }
        groundOps?.let { builder.groundOptions = it.toTypedArray() }
        inventoryOps?.let { builder.inventoryOptions = it.toTypedArray() }
        recolorSrc?.let { builder.recolorSrc = it.toIntArray() }
        recolorDest?.let { builder.recolorDest = it.toIntArray() }
        retextureSrc?.let { builder.retextureSrc = it.toIntArray() }
        retextureDest?.let { builder.retextureDest = it.toIntArray() }
        dropOpIndex?.let { builder.dropOptionIndex = it }
        resizeX?.let { builder.resizeX = it }
        resizeY?.let { builder.resizeY = it }
        resizeZ?.let { builder.resizeZ = it }
        ambient?.let { builder.ambient = it }
        contrast?.let { builder.contrast = it }
        exchangeable?.let { builder.exchangeable = it }
        team?.let { builder.team = it }
        noteLink?.let { builder.noteLink = it.toInt(names.items) }
        noteModel?.let { builder.noteModel = it.toInt(names.items) }
        placeholderLink?.let { builder.placeholderLink = it.toInt(names.items) }
        placeholderModel?.let { builder.placeholderModel = it.toInt(names.items) }
        boughtLink?.let { builder.boughtLink = it.toInt(names.items) }
        boughtValue?.let { builder.boughtValue = it.toInt(names.items) }
        countItem?.let { builder.countItem = it.toIntList(names.items).toIntArray() }
        countCo?.let { builder.countCo = it.toIntArray() }
        weight?.let { builder.weight = it }
        wearPos1?.let { builder.wearPos1 = it }
        wearPos2?.let { builder.wearPos2 = it }
        wearPos3?.let { builder.wearPos3 = it }
        params?.let { builder.params = it.toParamMap(names, paramTypes) }
        inherit?.let {
            val named = names.items.getOrThrow(it.stripTag())
            builder += itemTypes.getValue(named.id)
        }
        return builder.build()
    }

    private companion object {

        private fun Map<String, String>.toParamMap(
            names: NamedTypeMapHolder,
            params: ParamTypeList
        ): ParamMap {
            val parameters = mutableMapOf<Int, Any>()
            forEach { (key, value) ->
                val paramId = key.toIntOrNull() ?: names.parameters[key.stripTag()]?.id
                val id = paramId ?: error("Param with name `$key` not found in cache.")
                val param = params[id] ?: error("Param with id `$paramId` not found in cache.")
                val type = param.type
                if (type == null) {
                    parameters[id] = value
                    return@forEach
                }
                val converted = value.stripTag().convert(names, type)
                parameters[id] = converted
            }
            return ParamMap(parameters)
        }

        private fun Any.toInt(names: Map<String, NamedItem>): Int = when (this) {
            is Int -> this
            is String -> names[stripTag()]?.id ?: error("Item with name `$this` not found in cache.")
            else -> error("Could not convert `$this` to int value.")
        }

        private fun List<Any>.toIntList(names: Map<String, NamedItem>): List<Int> = map { it.toInt(names) }

        private fun Map<String, NamedItem>.getOrThrow(name: String): NamedItem =
            this[name] ?: error("Item with name `$name` not found in cache.")
    }
}
