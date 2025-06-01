package org.rsmod.api.type.script.dsl

import org.rsmod.game.dbtable.DbGroupColumn
import org.rsmod.game.dbtable.DbGroupListColumn
import org.rsmod.game.dbtable.DbSingleColumn
import org.rsmod.game.type.dbrow.DbRowTypeBuilder
import org.rsmod.game.type.dbrow.UnpackedDbRowType
import org.rsmod.game.type.dbtable.DbTableType
import org.rsmod.game.type.literal.CacheVarLiteral

@DslMarker private annotation class DbRowBuilderDsl

@DbRowBuilderDsl
public class DbRowPluginBuilder(public var internal: String? = null) {
    private val backing: DbRowTypeBuilder = DbRowTypeBuilder()
    private val data = mutableMapOf<Int, List<Any>>()
    private val types = mutableMapOf<Int, List<Int>>()

    public var table: DbTableType
        get() = throw UnsupportedOperationException()
        set(value) {
            backing.table = value.id
        }

    public fun build(id: Int): UnpackedDbRowType {
        backing.internal = internal
        backing.data = data
        backing.types = types
        backing.columnCount = if (types.isEmpty()) 0 else types.keys.max() + 1
        return backing.build(id)
    }

    public fun <T, R> column(type: DbSingleColumn<T, R>, init: SingleColumn<T, R>.() -> Unit) {
        assertTable()
        val builder = SingleColumn(type).apply(init)
        builder.apply(this)
    }

    public fun <T, R> columnGroup(type: DbGroupColumn<T, R>, init: GroupColumn<T, R>.() -> Unit) {
        assertTable()
        val builder = GroupColumn(type).apply(init)
        builder.apply(this)
    }

    public fun <T, R> columnGroupList(
        type: DbGroupListColumn<T, R>,
        init: GroupListColumn<T, R>.() -> Unit,
    ) {
        assertTable()
        val builder = GroupListColumn(type).apply(init)
        builder.apply(this)
    }

    private fun assertTable() {
        check(backing.table != null) { "`table` must be set before columns." }
    }

    @DbRowBuilderDsl
    public class SingleColumn<T, R>(private val column: DbSingleColumn<T, R>) {
        public var value: R? = null

        internal fun apply(builder: DbRowPluginBuilder) {
            val types = column.types
            if (types.size != 1) {
                val message = "Single columns can only support columns of one type: actual=$types"
                throw IllegalArgumentException(message)
            }

            val columnId = column.columnId
            if (columnId !in 0..127) {
                val message = "Column id must be within range [0..127]: $columnId"
                throw IllegalArgumentException(message)
            }

            if (builder.types.containsKey(columnId)) {
                throw IllegalStateException("Column already defined: '${column.name}'")
            }

            val actualTable = column.table
            val expectedTable = builder.backing.table
            if (actualTable != expectedTable) {
                val message =
                    "Row and column table must match: " +
                        "expected=$expectedTable, actual=$actualTable, column='${column.name}'"
                throw IllegalStateException(message)
            }

            builder.types[columnId] = types.map(CacheVarLiteral::id)

            val value = value ?: error("`value` must not be null. (column='${column.name}')")
            val encoded = column.encode(value) as Any
            builder.data[columnId] = listOf(encoded)
        }
    }

    @DbRowBuilderDsl
    public class GroupColumn<T, R>(private val column: DbGroupColumn<T, R>) {
        public var value: R? = null

        internal fun apply(builder: DbRowPluginBuilder) {
            val types = column.types
            if (types.size < 2) {
                val message =
                    "Group columns can only support columns with more than one type: actual=$types"
                throw IllegalArgumentException(message)
            }

            val columnId = column.columnId
            if (columnId !in 0..127) {
                val message = "Column id must be within range [0..127]: $columnId"
                throw IllegalArgumentException(message)
            }

            if (builder.types.containsKey(columnId)) {
                throw IllegalStateException("Column already defined: '${column.name}'")
            }

            val actualTable = column.table
            val expectedTable = builder.backing.table
            if (actualTable != expectedTable) {
                val message =
                    "Row and column table must match: " +
                        "expected=$expectedTable, actual=$actualTable, column='${column.name}'"
                throw IllegalStateException(message)
            }

            builder.types[columnId] = types.map(CacheVarLiteral::id)

            val value = value ?: error("`value` must not be null. (column='${column.name}')")
            val encoded = column.encode(value)
            builder.data[columnId] = encoded
        }
    }

    @DbRowBuilderDsl
    public class GroupListColumn<T, R>(private val column: DbGroupListColumn<T, R>) {
        public var values: List<R>? = null

        internal fun apply(builder: DbRowPluginBuilder) {
            val columnId = column.columnId
            if (columnId !in 0..127) {
                val message = "Column id must be within range [0..127]: $columnId"
                throw IllegalArgumentException(message)
            }

            if (builder.types.containsKey(columnId)) {
                throw IllegalStateException("Column already defined: '${column.name}'")
            }

            val actualTable = column.table
            val expectedTable = builder.backing.table
            if (actualTable != expectedTable) {
                val message =
                    "Row and column table must match: " +
                        "expected=$expectedTable, actual=$actualTable, column='${column.name}'"
                throw IllegalStateException(message)
            }

            builder.types[columnId] = column.types.map(CacheVarLiteral::id)

            val values = values ?: error("`values` must not be null. (column='${column.name}')")
            val encoded = values.flatMap(column::encode)
            builder.data[columnId] = encoded
        }
    }
}
