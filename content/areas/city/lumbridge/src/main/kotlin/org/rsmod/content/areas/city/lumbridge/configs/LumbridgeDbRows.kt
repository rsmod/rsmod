package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.areas
import org.rsmod.api.config.refs.dbcolumns
import org.rsmod.api.config.refs.dbtables
import org.rsmod.api.type.builders.dbrow.DbRowBuilder
import org.rsmod.api.type.refs.dbrow.DbRowReferences

typealias lumbridge_rows = LumbridgeDbRows

object LumbridgeDbRows : DbRowReferences() {
    val music_autumn_voyage = find("music_autumn_voyage")
    val music_book_of_spells = find("music_book_of_spells")
    val music_dream = find("music_dream")
    val music_flute_salad = find("music_flute_salad")
    val music_harmony = find("music_harmony")
    val music_yesteryear = find("music_yesteryear")
}

object LumbridgeDbRowBuilder : DbRowBuilder() {
    init {
        build("music_modern_lumbridge") {
            table = dbtables.music_modern
            column(dbcolumns.music_modern_area) { value = areas.lumbridge }
            columnGroupList(dbcolumns.music_modern_tracks) {
                values =
                    listOf(
                        lumbridge_rows.music_autumn_voyage,
                        lumbridge_rows.music_book_of_spells,
                        lumbridge_rows.music_dream,
                        lumbridge_rows.music_flute_salad,
                        lumbridge_rows.music_harmony,
                        lumbridge_rows.music_yesteryear,
                    )
            }
        }
    }
}
