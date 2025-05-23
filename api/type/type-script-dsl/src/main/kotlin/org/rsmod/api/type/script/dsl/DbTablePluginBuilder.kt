package org.rsmod.api.type.script.dsl

import org.rsmod.game.dbtable.DbGroupColumn
import org.rsmod.game.dbtable.DbGroupListColumn
import org.rsmod.game.dbtable.DbSingleColumn
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

    public fun <T, R> column(type: DbSingleColumn<T, R>, init: SingleColumn<T, R>.() -> Unit) {
        val builder = SingleColumn(type).apply(init)
        builder.apply(this)
    }

    public fun <T, R> columnGroup(type: DbGroupColumn<T, R>, init: GroupColumn<T, R>.() -> Unit) {
        val builder = GroupColumn(type).apply(init)
        builder.apply(this)
    }

    public fun <T, R> columnGroupList(
        type: DbGroupListColumn<T, R>,
        init: GroupListColumn<T, R>.() -> Unit,
    ) {
        val builder = GroupListColumn(type).apply(init)
        builder.apply(this)
    }

    @DbTableBuilderDsl
    public class SingleColumn<T, R>(private val column: DbSingleColumn<T, R>) {
        public var default: R? = null
        public var clientside: Boolean = false

        internal fun apply(builder: DbTablePluginBuilder) {
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

            builder.tables += column.table
            builder.types[columnId] = types.map(CacheVarLiteral::id)

            var attributes = builder.attributes[columnId] ?: 0
            attributes = (attributes and 0x80.inv()) or (columnId and 0x7F)

            if (clientside) {
                attributes = attributes or UnpackedDbTableType.CLIENTSIDE
            }

            val default = this.default
            if (default != null) {
                val encoded = column.encode(default) as Any
                builder.defaults[columnId] = listOf(encoded)
                attributes = attributes or UnpackedDbTableType.REQUIRED
            }

            builder.attributes[columnId] = attributes
        }
    }

    @DbTableBuilderDsl
    public class GroupColumn<T, R>(private val column: DbGroupColumn<T, R>) {
        public var default: R? = null
        public var clientside: Boolean = false

        internal fun apply(builder: DbTablePluginBuilder) {
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

            builder.tables += column.table
            builder.types[columnId] = types.map(CacheVarLiteral::id)

            var attributes = builder.attributes[columnId] ?: 0
            attributes = (attributes and 0x80.inv()) or (columnId and 0x7F)

            if (clientside) {
                attributes = attributes or UnpackedDbTableType.CLIENTSIDE
            }

            val default = this.default
            if (default != null) {
                val encoded = column.encode(default)
                builder.defaults[columnId] = encoded
                attributes = attributes or UnpackedDbTableType.REQUIRED
            }

            builder.attributes[columnId] = attributes
        }
    }

    @DbTableBuilderDsl
    public class GroupListColumn<T, R>(private val column: DbGroupListColumn<T, R>) {
        public var default: R? = null
        public var clientside: Boolean = false

        internal fun apply(builder: DbTablePluginBuilder) {
            val types = column.types
            if (types.size < 2) {
                val message =
                    "Group list columns can only support columns with more " +
                        "than one type: actual=$types"
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

            builder.tables += column.table
            builder.types[columnId] = types.map(CacheVarLiteral::id)

            var attributes = builder.attributes[columnId] ?: 0
            attributes = (attributes and 0x80.inv()) or (columnId and 0x7F)

            if (clientside) {
                attributes = attributes or UnpackedDbTableType.CLIENTSIDE
            }

            val default = this.default
            if (default != null) {
                val encoded = column.encode(default)
                builder.defaults[columnId] = encoded
                attributes = attributes or UnpackedDbTableType.REQUIRED
            }

            builder.attributes[columnId] = attributes or UnpackedDbTableType.LIST
        }
    }
}
