package org.rsmod.game.obj

import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.UncheckedType

public data class InvObj
@UncheckedType("Use the `ObjType` constructor instead for type-safety consistency.")
constructor(public val id: Int, public val count: Int, public val vars: Int = 0) {
    @OptIn(UncheckedType::class)
    public constructor(type: ObjType, count: Int = 1, vars: Int = 0) : this(type.id, count, vars)
}
