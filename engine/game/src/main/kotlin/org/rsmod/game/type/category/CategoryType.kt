package org.rsmod.game.type.category

import org.rsmod.game.type.CacheType

public data class CategoryType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "CategoryType(internalName='$internalName', internalId=$internalId)"
}
