package org.rsmod.game.model.stat

import com.google.common.base.MoreObjects

open class StatKey(val id: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StatKey) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return Integer.hashCode(id)
    }

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("id", id)
        .toString()
}
