package org.runerealm.api.music.configs

import org.runerealm.api.music.MusicVariable
import org.runerealm.api.type.refs.dbcol.DbColumnReferences
import org.runerealm.api.type.refs.dbtable.DbTableReferences
import org.runerealm.game.dbtable.DbColumnCodec
import org.runerealm.game.type.TypeListMap
import org.runerealm.game.type.literal.CacheVarLiteral

internal typealias music_columns = MusicDbColumns

internal typealias music_tables = MusicDbTables

internal object MusicDbColumns : DbColumnReferences() {
    val displayName = string("music:displayname")
    val unlockHint = string("music:unlockhint")
    val duration = int("music:duration")
    val midi = midi("music:midi")
    val variable = value("music:variable", VariableCodec)
    val hidden = boolean("music:hidden")
    val secondary_track = dbRow("music:secondary_track")
}

internal object MusicDbTables : DbTableReferences() {
    val music = find("music")
}

private object VariableCodec : DbColumnCodec<Int, MusicVariable> {
    override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.INT, CacheVarLiteral.INT)

    override fun decode(
        iterator: DbColumnCodec.Iterator<Int, MusicVariable>,
        types: TypeListMap,
    ): MusicVariable {
        val varpIndex = iterator.next()
        val bitflag = iterator.next()
        return MusicVariable(varpIndex, bitflag)
    }

    override fun encode(value: MusicVariable): List<Int> {
        return listOf(value.varpIndex, value.bitpos)
    }
}
