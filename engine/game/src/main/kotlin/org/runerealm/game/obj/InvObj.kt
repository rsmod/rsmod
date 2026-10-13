package org.runerealm.game.obj

import kotlin.contracts.contract
import org.runerealm.game.type.obj.ObjType

/** @deprecated Moved to [org.runerealm.game.inv.InvObj]. Will be removed before version 1.0.0. */
@Deprecated(
    message = "Moved to org.runerealm.game.inv.InvObj. Will be removed before version 1.0.0",
    replaceWith = ReplaceWith("org.runerealm.game.inv.InvObj"),
    level = DeprecationLevel.ERROR,
)
public typealias InvObj = org.runerealm.game.inv.InvObj

/** @deprecated Moved to org.runerealm.game.inv package. Will be removed before version 1.0.0. */
@Deprecated(
    message =
        "Import from org.runerealm.game.inv package instead. Will be removed before version 1.0.0",
    level = DeprecationLevel.ERROR,
)
public fun org.runerealm.game.inv.InvObj?.isType(type: ObjType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this != null && type.internalId == id
}

/** @deprecated Moved to org.runerealm.game.inv package. Will be removed before version 1.0.0. */
@Deprecated(
    message =
        "Import from org.runerealm.game.inv package instead. Will be removed before version 1.0.0",
    level = DeprecationLevel.ERROR,
)
public fun org.runerealm.game.inv.InvObj?.isAnyType(type1: ObjType, type2: ObjType): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.internalId == id || type2.internalId == id)
}

/** @deprecated Moved to org.runerealm.game.inv package. Will be removed before version 1.0.0. */
@Deprecated(
    message =
        "Import from org.runerealm.game.inv package instead. Will be removed before version 1.0.0",
    level = DeprecationLevel.ERROR,
)
public fun org.runerealm.game.inv.InvObj?.isAnyType(
    type1: ObjType,
    type2: ObjType,
    type3: ObjType,
): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null &&
        (type1.internalId == id || type2.internalId == id || type3.internalId == id)
}

/** @deprecated Moved to org.runerealm.game.inv package. Will be removed before version 1.0.0. */
@Deprecated(
    message =
        "Import from org.runerealm.game.inv package instead. Will be removed before version 1.0.0",
    level = DeprecationLevel.ERROR,
)
public fun org.runerealm.game.inv.InvObj?.isAnyType(
    type1: ObjType,
    type2: ObjType,
    type3: ObjType,
    type4: ObjType,
): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null &&
        (type1.internalId == id ||
            type2.internalId == id ||
            type3.internalId == id ||
            type4.internalId == id)
}

/** @deprecated Moved to org.runerealm.game.inv package. Will be removed before version 1.0.0. */
@Deprecated(
    message =
        "Import from org.runerealm.game.inv package instead. Will be removed before version 1.0.0",
    level = DeprecationLevel.ERROR,
)
public fun org.runerealm.game.inv.InvObj?.isAnyType(
    type1: ObjType,
    type2: ObjType,
    vararg types: ObjType,
): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null &&
        (type1.internalId == id || type2.internalId == id || types.any { it.internalId == id })
}
