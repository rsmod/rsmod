package org.rsmod.game.dbtable

import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.dbrow.DbRowTypeList
import org.rsmod.game.type.dbrow.UnpackedDbRowType
import org.rsmod.game.type.dbtable.DbTableTypeList
import org.rsmod.game.type.dbtable.UnpackedDbTableType

public class DbRowResolver(private val cacheTypes: TypeListMap) {
    private val rows: DbRowTypeList by cacheTypes::dbRows
    private val tables: DbTableTypeList by cacheTypes::dbTables

    public operator fun get(row: DbRowType): DbRow {
        val unpacked = rows[row]
        val table = tables[unpacked.table]
        if (table == null) {
            val message = "DbTable associated with row does not exist: row=$unpacked"
            throw IllegalStateException(message)
        }
        return get(table, unpacked)
    }

    public operator fun get(table: UnpackedDbTableType, row: UnpackedDbRowType): DbRow {
        return DbRow(cacheTypes, table, row)
    }
}
