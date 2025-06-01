package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.dbcol.DbColumnReferences
import org.rsmod.game.dbtable.DbColumnCodec
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.literal.CacheVarLiteral

typealias dbcolumns = BaseDbColumns

object BaseDbColumns : DbColumnReferences() {
    val music_classic_area = area("music_classic:area")
    val music_classic_track = dbRow("music_classic:track")
    val music_classic_auto_script = boolean("music_classic:auto_script")
    val music_modern_area = area("music_modern:area")
    val music_modern_tracks = list("music_modern:tracks", DbRowListTypeCodec)
    val music_modern_auto_script = boolean("music_modern:auto_script")
}

private object DbRowListTypeCodec : DbColumnCodec<Any, DbRowType> {
    override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.DBROW)

    override fun decode(
        iterator: DbColumnCodec.Iterator<Any, DbRowType>,
        types: TypeListMap,
    ): DbRowType {
        val type = iterator.nextInt()
        return types.dbRows.getValue(type).toHashedType()
    }

    override fun encode(value: DbRowType): Any {
        return listOf(value.id)
    }
}
