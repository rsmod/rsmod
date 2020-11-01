package org.rsmod.game.model.obj.type

import org.rsmod.game.cache.ConfigType
import org.rsmod.game.cache.ConfigTypeList

data class ObjectType(
    override val id: Int,
    val name: String,
    val width: Int,
    val length: Int,
    val blockPath: Boolean,
    val blockProjectile: Boolean,
    val interact: Boolean,
    val obstruct: Boolean,
    val clipMask: Int,
    val varp: Int,
    val varbit: Int,
    val animation: Int,
    val rotated: Boolean,
    val options: List<String?>,
    val transforms: List<Int>
) : ConfigType

class ObjectTypeList : ConfigTypeList<ObjectType>()
