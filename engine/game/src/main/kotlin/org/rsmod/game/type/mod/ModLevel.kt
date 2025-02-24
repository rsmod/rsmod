package org.rsmod.game.type.mod

import org.rsmod.game.type.CacheType

public data class ModLevel(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String = "ModLevel(internalId=$internalId, internalName=$internalName)"
}
