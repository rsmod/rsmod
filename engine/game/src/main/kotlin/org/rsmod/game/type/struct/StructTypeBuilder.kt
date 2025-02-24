package org.rsmod.game.type.struct

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectParamMap
import org.rsmod.game.type.util.ParamMap

@DslMarker private annotation class StructBuilderDsl

@StructBuilderDsl
public class StructTypeBuilder(public var internal: String? = null) {
    public var paramMap: ParamMap? = null

    public fun build(id: Int): UnpackedStructType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        return UnpackedStructType(paramMap = paramMap, internalId = id, internalName = internal)
    }

    public companion object {
        public fun merge(edit: UnpackedStructType, base: UnpackedStructType): UnpackedStructType {
            val paramMap = selectParamMap(edit, base) { paramMap }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedStructType(
                paramMap = paramMap,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
