package org.rsmod.game.type.controller

import org.rsmod.game.type.CacheType

public data class ControllerType(
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    override fun toString(): String =
        "ControllerType(internalName='$internalName', internalId=$internalId)"
}
