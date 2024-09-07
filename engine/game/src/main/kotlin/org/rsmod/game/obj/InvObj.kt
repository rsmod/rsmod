@file:Suppress("DEPRECATION")

package org.rsmod.game.obj

import kotlin.contracts.contract
import org.rsmod.game.type.obj.ObjType

public data class InvObj
@Deprecated("Use the `ObjType` constructor instead for type-safety consistency.")
constructor(public val id: Int, public val count: Int, public val vars: Int = 0) {
    public constructor(type: ObjType, count: Int = 1, vars: Int = 0) : this(type.id, count, vars)

    public infix fun isAssociatedWith(type: ObjType): Boolean = type.internalId == id
}

public infix fun InvObj?.isAssociatedWith(type: ObjType): Boolean {
    contract { returns(true) implies (this@isAssociatedWith != null) }
    return isAssociatedWith(type)
}
