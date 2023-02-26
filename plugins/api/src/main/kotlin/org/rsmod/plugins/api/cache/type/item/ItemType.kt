package org.rsmod.plugins.api.cache.type.item

import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.param.ParamMap

public data class ItemType(
    override val id: Int,
    val internalName: String?,
    val name: String,
    val stacks: Boolean,
    val cost: Int,
    val members: Boolean,
    val groundOptions: List<String?>,
    val inventoryOptions: List<String?>,
    val exchangeable: Boolean,
    val team: Int,
    val noteLink: Int,
    val noteModel: Int,
    val placeholderLink: Int,
    val placeholderModel: Int,
    val params: ParamMap?,
    val model: Int,
    val zoom2d: Int,
    val xan2d: Int,
    val yan2d: Int,
    val zan2d: Int,
    val xoff2d: Int,
    val yoff2d: Int,
    val maleModelOffset: Int,
    val femaleModelOffset: Int,
    val maleModel0: Int,
    val maleModel1: Int,
    val maleModel2: Int,
    val femaleModel0: Int,
    val femaleModel1: Int,
    val femaleModel2: Int,
    val maleHeadModel0: Int,
    val maleHeadModel1: Int,
    val femaleHeadModel0: Int,
    val femaleHeadModel1: Int,
    val categories: Set<Int>,
    val recolorSrc: List<Int>,
    val recolorDest: List<Int>,
    val retextureSrc: List<Int>,
    val retextureDest: List<Int>,
    val dropOptionIndex: Int,
    val resizeX: Int,
    val resizeY: Int,
    val resizeZ: Int,
    val ambient: Int,
    val contrast: Int,
    val boughtLink: Int,
    val boughtValue: Int,
    val countItem: List<Int>,
    val countCo: List<Int>,
    val weight: Int,
    val wearPos1: Int,
    val wearPos2: Int,
    val wearPos3: Int
) : ConfigType {

    public val isNoted: Boolean get() = noteModel > 0
    public val canBeNoted: Boolean get() = noteModel == 0 && noteLink > 0

    public val isPlaceholder: Boolean get() = placeholderModel > 0
    public val hasPlaceholder: Boolean get() = placeholderModel == 0 && placeholderLink > 0
}
