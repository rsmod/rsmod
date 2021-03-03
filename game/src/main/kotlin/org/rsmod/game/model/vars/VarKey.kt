package org.rsmod.game.model.vars

import com.google.common.base.MoreObjects

sealed class VarKey<T>

open class AttributeKey<T> private constructor(
    val persistenceId: String?
) : VarKey<T>() {

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

open class VarpKey<T> private constructor(val id: Int) : VarKey<T>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VarpKey<*>
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return Integer.hashCode(id)
    }

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("hashCode", hashCode())
        .toString()

    companion object {

        fun <T> create(id: Int): VarpKey<T> {
            return VarpKey(id)
        }
    }
}
