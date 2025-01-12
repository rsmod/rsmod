package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.struct.StructTypeBuilder
import org.rsmod.game.type.struct.UnpackedStructType
import org.rsmod.game.type.util.ParamMapBuilder

@DslMarker private annotation class StructBuilderDsl

@StructBuilderDsl
public class StructPluginBuilder(public var internal: String? = null) {
    private val backing: StructTypeBuilder = StructTypeBuilder()

    public var param: ParamMapBuilder = ParamMapBuilder()

    public fun build(id: Int): UnpackedStructType {
        backing.internal = internal
        if (param.isNotEmpty()) {
            backing.paramMap = param.toParamMap()
        }
        return backing.build(id)
    }
}
