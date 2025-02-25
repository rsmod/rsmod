package org.rsmod.game.obj

import kotlin.contracts.contract
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.UncheckedType

@OptIn(UncheckedType::class)
public data class InvObj
@UncheckedType("Use the `ObjType` constructor instead for type-safety consistency.")
constructor(public val id: Int, public val count: Int, public val vars: Int = 0) {
    public constructor(copy: InvObj) : this(copy.id, copy.count, copy.vars)

    public constructor(type: ObjType, count: Int = 1, vars: Int = 0) : this(type.id, count, vars)
}

public fun InvObj?.isType(type: ObjType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this != null && type.id == id
}

public fun InvObj?.isAnyType(type1: ObjType, type2: ObjType): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id)
}

public fun InvObj?.isAnyType(type1: ObjType, type2: ObjType, type3: ObjType): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id || type3.id == id)
}

public fun InvObj?.isAnyType(
    type1: ObjType,
    type2: ObjType,
    type3: ObjType,
    type4: ObjType,
): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id || type3.id == id || type4.id == id)
}
