package org.rsmod.game.model.attr

import com.google.common.base.MoreObjects

@Suppress("UNUSED")
open class AttributeKey<T> private constructor(val persistenceId: String?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AttributeKey<*>
        if (persistenceId != other.persistenceId) return false
        return true
    }

    override fun hashCode(): Int {
        return persistenceId?.hashCode() ?: super.hashCode()
    }

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("persistenceId", persistenceId)
        .add("hashCode", hashCode())
        .toString()

    companion object {

        fun <T> createTemp(): AttributeKey<T> {
            return AttributeKey(null)
        }

        fun <T> createPersistent(uniqueId: String): AttributeKey<T> {
            return AttributeKey(uniqueId)
        }
    }
}
