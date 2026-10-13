package org.runerealm.api.type.script.dsl

import org.runerealm.game.type.struct.StructTypeBuilder
import org.runerealm.game.type.struct.UnpackedStructType
import org.runerealm.game.type.util.ParamMapBuilder

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
