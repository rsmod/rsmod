package org.runerealm.game.type.content

import org.runerealm.game.type.CacheType

public data class ContentGroupType(
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    override fun toString(): String =
        "ContentGroupType(internalName='$internalName', internalId=$internalId)"
}
