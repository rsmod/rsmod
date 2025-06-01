package org.rsmod.api.config.builders

import org.rsmod.api.config.refs.dbcolumns
import org.rsmod.api.type.builders.dbtable.DbTableBuilder

internal object DbTableBuilds : DbTableBuilder() {
    init {
        build("music_classic") {
            column(dbcolumns.music_classic_area)
            column(dbcolumns.music_classic_track)
            column(dbcolumns.music_classic_auto_script) { default = true }
        }

        build("music_modern") {
            column(dbcolumns.music_modern_area)
            columnList(dbcolumns.music_modern_tracks)
            column(dbcolumns.music_modern_auto_script) { default = true }
        }
    }
}
