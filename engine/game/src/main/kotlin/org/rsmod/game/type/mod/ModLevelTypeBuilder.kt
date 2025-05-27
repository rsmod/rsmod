package org.rsmod.game.type.mod

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

public class ModLevelTypeBuilder(public var internal: String? = null) {
    public var clientCode: Int? = null
    public var accessFlags: Long? = null

    public fun build(id: Int): UnpackedModLevelType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        require(id < Long.SIZE_BITS) { "`id` must be within range [0..63]. ('$internal':$id)" }
        val clientCode = clientCode ?: 0
        val accessFlags = accessFlags ?: 0
        return UnpackedModLevelType(
            clientCode = clientCode,
            accessFlags = accessFlags,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedModLevelType> {
        override fun merge(
            edit: UnpackedModLevelType,
            base: UnpackedModLevelType,
        ): UnpackedModLevelType {
            val clientCode = select(edit, base, default = 0) { clientCode }
            val accessFlags = select(edit, base, default = 0) { accessFlags }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedModLevelType(
                clientCode = clientCode,
                accessFlags = accessFlags,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
