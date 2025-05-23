package org.rsmod.game.type.dbtable

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectMap
import org.rsmod.game.type.util.GenericPropertySelector.selectPredicate
import org.rsmod.game.type.util.MergeableCacheBuilder

public class DbTableTypeBuilder(public var internal: String? = null) {
    public var types: Map<Int, List<Int>>? = null
    public var defaults: Map<Int, List<Any>>? = null
    public var attributes: Map<Int, Int>? = null
    public var columnTables: Set<Int>? = null
    public var columnCount: Int = 0

    public fun build(id: Int): UnpackedDbTableType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val types = types ?: emptyMap()
        val defaults = defaults ?: emptyMap()
        val attributes = attributes ?: emptyMap()
        val tables = columnTables ?: emptySet()
        return UnpackedDbTableType(
            types = types,
            defaults = defaults,
            attributes = attributes,
            columnTables = tables,
            columnCount = columnCount,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedDbTableType> {
        public const val DEFAULT_ATTRIBUTES: Int = UnpackedDbTableType.CLIENTSIDE

        override fun merge(
            edit: UnpackedDbTableType,
            base: UnpackedDbTableType,
        ): UnpackedDbTableType {
            val types = selectMap(edit, base) { types }
            val defaults = selectMap(edit, base) { defaults }
            val attributes = selectMap(edit, base) { attributes }
            val tables =
                selectPredicate(edit.columnTables, base.columnTables) {
                    edit.columnTables.isNotEmpty()
                }
            val columnCount = select(edit, base, default = 0) { columnCount }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedDbTableType(
                types = types,
                defaults = defaults,
                attributes = attributes,
                columnTables = tables,
                columnCount = columnCount,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
