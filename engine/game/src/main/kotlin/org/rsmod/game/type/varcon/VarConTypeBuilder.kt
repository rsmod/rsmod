package org.rsmod.game.type.varcon

@DslMarker private annotation class VarConBuilderDsl

@VarConBuilderDsl
public class VarConTypeBuilder(public var internal: String) {
    public fun build(id: Int): UnpackedVarConType = UnpackedVarConType(id, internal)
}
