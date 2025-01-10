package org.rsmod.game.type.controller

@DslMarker private annotation class ControllerBuilderDsl

@ControllerBuilderDsl
public class ControllerTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): ControllerType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return ControllerType(internalId = id, internalName = internalName)
    }
}
