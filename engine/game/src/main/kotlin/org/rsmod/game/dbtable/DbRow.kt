package org.rsmod.game.dbtable

import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.dbrow.UnpackedDbRowType
import org.rsmod.game.type.dbtable.UnpackedDbTableType

public class DbRow(
    private val cacheTypes: TypeListMap,
    private val table: UnpackedDbTableType,
    private val row: UnpackedDbRowType,
) {
    public val type: UnpackedDbRowType
        get() = row

    public operator fun <T, R> get(column: DbSingleColumn<T, R>): R {
        val single = getOrNull(column)
        if (single == null) {
            val message =
                "Row '${row.internalName}' has not defined the '${column.internalName}' column " +
                    "and the table does not contain a default value. Use `getOrNull` if applicable."
            throw IllegalStateException(message)
        }
        return single
    }

    public operator fun <T, R> get(column: DbGroupColumn<T, R>): R {
        val single = getOrNull(column)
        if (single == null) {
            val message =
                "Row '${row.internalName}' has not defined the '${column.internalName}' column " +
                    "and the table does not contain a default value. Use `getOrNull` if applicable."
            throw IllegalStateException(message)
        }
        return single
    }

    public operator fun <T, R> get(column: DbListColumn<T, R>): List<R> {
        val values = getOrNull(column)
        if (values == null) {
            val message =
                "Row '${row.internalName}' has not defined the '${column.internalName}' column " +
                    "and the table does not contain a default value. Use `getOrNull` if applicable."
            throw IllegalStateException(message)
        }
        return values
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T, R> getOrNull(column: DbSingleColumn<T, R>): R? {
        val values = getOrDefault(column) ?: return null
        if (values.size > 1) {
            val message =
                "Column has a group of values: $column (row='${row.internalName}', values=$values)"
            throw IllegalStateException(message)
        }
        val single = values.single() as T
        return column.decode(cacheTypes, single)
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T, R> getOrNull(column: DbGroupColumn<T, R>): R? {
        val values = getOrDefault(column) ?: return null
        return column.decode(cacheTypes, values as List<T>)
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T, R> getOrNull(column: DbListColumn<T, R>): List<R>? {
        val values = getOrDefault(column) ?: return null
        return column.decode(cacheTypes, values as List<T>)
    }

    private fun <T, R> getOrDefault(column: DbColumn<T, R>): List<Any>? {
        return row.data[column.columnId] ?: table.defaults[column.columnId]
    }

    override fun toString(): String {
        return "DbRow(${table.internalName}:${row.internalName})"
    }
}
