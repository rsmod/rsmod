package org.rsmod.api.type.script.dsl

import org.rsmod.game.dbtable.DbListColumn
import org.rsmod.game.dbtable.DbValueColumn
import org.rsmod.game.type.dbtable.DbTableTypeBuilder
import org.rsmod.game.type.dbtable.UnpackedDbTableType
import org.rsmod.game.type.literal.CacheVarLiteral

@DslMarker private annotation class DbTableBuilderDsl

@DbTableBuilderDsl
public class DbTablePluginBuilder(public var internal: String? = null) {
    private val backing: DbTableTypeBuilder = DbTableTypeBuilder()

    private val types = mutableMapOf<Int, List<Int>>()
    private val defaults = mutableMapOf<Int, List<Any>>()
    private val attributes = mutableMapOf<Int, Int>()
    private val tables = mutableSetOf<Int>()

    public fun build(id: Int): UnpackedDbTableType {
        backing.internal = internal
        backing.types = types
        backing.defaults = defaults
        backing.attributes = attributes
        backing.columnTables = tables
        backing.columnCount = types.keys.max() + 1
        return backing.build(id)
    }

    public fun <T, R> column(type: DbValueColumn<T, R>, init: ValueColumn<T, R>.() -> Unit = {}) {
        val builder = ValueColumn(type).apply(init)
        builder.apply(this)
    }

    public fun <T, R> columnList(type: DbListColumn<T, R>, init: ListColumn<T, R>.() -> Unit = {}) {
        val builder = ListColumn(type).apply(init)
        builder.apply(this)
    }

    @DbTableBuilderDsl
    public class ValueColumn<T, R>(private val column: DbValueColumn<T, R>) {
        public var default: R? = null
        public var clientside: Boolean = false

        @Suppress("UNCHECKED_CAST")
        internal fun apply(builder: DbTablePluginBuilder) {
            val columnId = column.columnId
            if (columnId !in 0..127) {
                val message = "Column id must be within range [0..127]: $columnId"
                throw IllegalArgumentException(message)
            }

            if (builder.types.containsKey(columnId)) {
                throw IllegalStateException("Column already defined: '${column.name}'")
            }

            builder.tables += column.table
            builder.types[columnId] = column.types.map(CacheVarLiteral::id)

            var attributes = builder.attributes[columnId] ?: 0
            attributes = (attributes and 0x80.inv()) or (columnId and 0x7F)

            if (clientside) {
                attributes = attributes or UnpackedDbTableType.CLIENTSIDE
            }

            val default = this.default
            if (default != null) {
                val encoded = column.encode(default)
                builder.defaults[columnId] = encoded as List<Any>
                attributes = attributes or UnpackedDbTableType.REQUIRED
            }

            builder.attributes[columnId] = attributes
        }
    }

    @DbTableBuilderDsl
    public class ListColumn<T, R>(private val column: DbListColumn<T, R>) {
        public var default: List<R>? = null
        public var clientside: Boolean = false

        @Suppress("UNCHECKED_CAST")
        internal fun apply(builder: DbTablePluginBuilder) {
            val columnId = column.columnId
            if (columnId !in 0..127) {
                val message = "Column id must be within range [0..127]: $columnId"
                throw IllegalArgumentException(message)
            }

            if (builder.types.containsKey(columnId)) {
                throw IllegalStateException("Column already defined: '${column.name}'")
            }

            builder.tables += column.table
            builder.types[columnId] = column.types.map(CacheVarLiteral::id)

            var attributes = builder.attributes[columnId] ?: 0
            attributes = (attributes and 0x80.inv()) or (columnId and 0x7F)

            if (clientside) {
                attributes = attributes or UnpackedDbTableType.CLIENTSIDE
            }

            val default = this.default
            if (default != null) {
                val encoded = default.flatMap(column::encode)
                builder.defaults[columnId] = encoded as List<Any>
                attributes = attributes or UnpackedDbTableType.REQUIRED
            }

            builder.attributes[columnId] = attributes or UnpackedDbTableType.LIST
        }
    }
}
