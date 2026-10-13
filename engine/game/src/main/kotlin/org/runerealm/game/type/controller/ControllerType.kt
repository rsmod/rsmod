package org.runerealm.game.type.controller

import org.runerealm.game.type.CacheType

public data class ControllerType(
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    override fun toString(): String =
        "ControllerType(internalName='$internalName', internalId=$internalId)"
}
