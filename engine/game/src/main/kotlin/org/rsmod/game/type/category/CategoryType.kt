package org.rsmod.game.type.category

import kotlin.contracts.contract
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.CacheType

public data class CategoryType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "CategoryType(internalName='$internalName', internalId=$internalId)"

    public fun isType(other: CategoryType): Boolean = other.internalId == internalId
}

public fun CategoryType?.isAssociatedWith(obj: InvObj?): Boolean {
    contract { returns(true) implies (this@isAssociatedWith != null && obj != null) }
    return this != null && obj != null && obj.id == id
}

public fun CategoryType?.isType(other: CategoryType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this != null && this.id == other.id
}
