package org.rsmod.game.type.dbrow

import org.rsmod.game.type.util.GenericPropertySelector.mergeMap
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

public class DbRowTypeBuilder(public var internal: String? = null) {
    public var table: Int? = null
    public var data: Map<Int, List<Any>>? = null
    public var types: Map<Int, List<Int>>? = null
    public var columnCount: Int = 0

    public fun build(id: Int): UnpackedDbRowType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val table = checkNotNull(table) { "`table` must be set." }
        val data = data ?: emptyMap()
        val types = types ?: emptyMap()
        return UnpackedDbRowType(
            table = table,
            data = data,
            types = types,
            columnCount = columnCount,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedDbRowType> {
        override fun merge(edit: UnpackedDbRowType, base: UnpackedDbRowType): UnpackedDbRowType {
            val table = select(edit, base, default = null) { table }
            val data = mergeMap(edit, base) { data }
            val types = mergeMap(edit, base) { types }
            val columnCount = select(edit, base, default = 0) { columnCount }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedDbRowType(
                table = table,
                data = data,
                types = types,
                columnCount = columnCount,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
