package org.runerealm.game.dbtable

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import org.runerealm.game.type.TypeListMap
import org.runerealm.game.type.dbrow.DbRowTypeList
import org.runerealm.game.type.dbtable.DbTableType
import org.runerealm.game.type.dbtable.DbTableTypeList

public class DbTableResolver(private val cacheTypes: TypeListMap) {
    private val tableRows by lazy { associateTableRows() }

    private val rows: DbRowTypeList by cacheTypes::dbRows
    private val tables: DbTableTypeList by cacheTypes::dbTables

    public operator fun get(table: DbTableType): List<DbRow> {
        val table = tables[table]
        val rowList = tableRows[table.id] ?: return emptyList()
        return rowList.map { DbRow(cacheTypes, table, rows.getValue(it)) }
    }

    private fun associateTableRows(): Int2ObjectMap<IntSet> {
        val mapped = Int2ObjectOpenHashMap<IntSet>()
        for (row in rows.values) {
            val list = mapped.computeIfAbsent(row.table) { IntArraySet() }
            list.add(row.id)
        }
        return mapped
    }
}
