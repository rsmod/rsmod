package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.dbcol.DbColumnReferences
import org.rsmod.game.dbtable.DbColumnCodec

typealias dbcolumns = BaseDbColumns

object BaseDbColumns : DbColumnReferences() {
    val music_classic_area = area("music_classic:area")
    val music_classic_track = dbRow("music_classic:track")
    val music_classic_auto_script = boolean("music_classic:auto_script")
    val music_modern_area = area("music_modern:area")
    val music_modern_tracks = list("music_modern:tracks", DbColumnCodec.DbRowTypeCodec)
    val music_modern_auto_script = boolean("music_modern:auto_script")
}
