package org.rsmod.plugins.api.config.obj

import org.rsmod.game.model.obj.type.ObjectTypeBuilder

data class ObjectConfig(
    val id: Int,
    val inherit: Int?,
    val dataFile: String?,
    val pack: Boolean,
    val builder: ObjectTypeBuilder
)
