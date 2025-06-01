package org.rsmod.game.dbtable

import kotlin.getValue
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.literal.CacheVarLiteral

public abstract class DbColumn<T, R> {
    internal var internalId: Int? = null
    internal var internalName: String? = null

    public val packed: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val table: Int
        get() = (packed shr 16) and 0xFFFF

    public val columnId: Int
        get() = packed and 0xFFFF

    public val name: String
        get() = internalName ?: error("`internalName` must not be null.")

    public abstract fun decode(types: TypeListMap, value: T): R
}

/** A column that stores a single value. */
public class DbSingleColumn<T, R>(private val codec: DbColumnCodec<T, R>) : DbColumn<T, R>() {
    public val types: List<CacheVarLiteral> by codec::types

    override fun decode(types: TypeListMap, value: T): R {
        val iterator = DbColumnCodec.Iterator(codec, listOf(value), types)
        return iterator.single()
    }

    public fun encode(value: R): T {
        return codec.encode(value)
    }
}

/** A column that stores multiple values used to construct a single decoded result. */
public class DbGroupColumn<T, R>(private val codec: DbColumnCodec<T, R>) : DbColumn<List<T>, R>() {
    public val types: List<CacheVarLiteral> by codec::types

    override fun decode(types: TypeListMap, value: List<T>): R {
        val iterator = DbColumnCodec.Iterator(codec, value, types)
        return iterator.single()
    }

    @Suppress("UNCHECKED_CAST")
    public fun encode(value: R): List<Any> {
        return codec.encode(value) as List<Any>
    }
}

/** A column that stores multiple values, each used to construct a decoded result. */
public class DbListColumn<T, R>(private val codec: DbColumnCodec<T, R>) :
    DbColumn<List<T>, List<R>>() {
    public val types: List<CacheVarLiteral> by codec::types

    override fun decode(types: TypeListMap, value: List<T>): List<R> {
        val iterator = DbColumnCodec.Iterator(codec, value, types)
        return iterator.toList()
    }

    @Suppress("UNCHECKED_CAST")
    public fun encode(value: R): List<Any> {
        return codec.encode(value) as List<Any>
    }
}
