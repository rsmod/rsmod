package org.rsmod.api.music

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import jakarta.inject.Inject
import org.rsmod.api.config.refs.dbcolumns
import org.rsmod.api.config.refs.dbtables
import org.rsmod.api.music.configs.music_columns
import org.rsmod.api.music.configs.music_tables
import org.rsmod.api.music.configs.music_varps
import org.rsmod.api.random.GameRandom
import org.rsmod.game.dbtable.DbTableResolver
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.varp.VarpType

public class MusicRepository
@Inject
constructor(private val random: GameRandom, private val dbTables: DbTableResolver) {
    private lateinit var musicRows: Int2ObjectMap<Music>
    private lateinit var musicIds: Int2ObjectMap<Music>

    private lateinit var modernAreas: Int2ObjectMap<List<Music>>
    private lateinit var classicAreas: Int2ObjectMap<Music>

    public fun forRow(row: DbRowType): Music? = musicRows[row.id]

    public fun forId(id: Int): Music? = musicIds[id]

    public fun getModernArea(area: AreaType): List<Music>? {
        return modernAreas[area.id]
    }

    public fun getClassicArea(area: AreaType): Music? {
        return classicAreas[area.id]
    }

    public fun getAll(): Collection<Music> {
        return musicRows.values
    }

    public fun load() {
        val unlockVarps = unlockVarps()

        val musicRows = loadMusicRows(unlockVarps)
        this.musicRows = Int2ObjectOpenHashMap(musicRows)

        val musicSlots = mapMusicById(musicRows)
        this.musicIds = Int2ObjectOpenHashMap(musicSlots)

        val modernAreas = loadModernAreas(musicRows)
        this.modernAreas = Int2ObjectOpenHashMap(modernAreas)

        val classicAreas = loadClassicAreas(musicRows)
        this.classicAreas = Int2ObjectOpenHashMap(classicAreas)
    }

    private fun loadMusicRows(unlockVarps: List<VarpType>): Map<Int, Music> {
        val rows = dbTables[music_tables.music]
        val mapped = mutableMapOf<Int, Music>()
        var currId = 1
        for (row in rows) {
            val displayName = row[music_columns.displayName]
            val unlockHint = row[music_columns.unlockHint]
            val midi = row[music_columns.midi]
            val variable = row[music_columns.variable]
            val duration = row[music_columns.duration]
            val hidden = row[music_columns.hidden]
            val secondary = row.getOrNull(music_columns.secondary_track)
            val unlockVarp = unlockVarps.getOrNull(variable.varpIndex - 1)
            val music =
                Music(
                    id = currId++,
                    displayName = displayName,
                    unlockHint = unlockHint,
                    duration = duration,
                    midi = midi,
                    unlockVarp = unlockVarp,
                    unlockBitpos = variable.bitpos,
                    hidden = hidden,
                    secondary = secondary,
                )
            mapped[row.type.id] = music
        }
        return mapped
    }

    private fun mapMusicById(musicRows: Map<Int, Music>): Map<Int, Music> {
        return musicRows.values.associateBy(Music::id)
    }

    private fun loadModernAreas(musicRows: Map<Int, Music>): Map<Int, List<Music>> {
        val rows = dbTables[dbtables.music_modern]
        val grouped = mutableMapOf<Int, MutableList<Music>>()
        for (row in rows) {
            val area = row[dbcolumns.music_modern_area]
            val trackRows = row[dbcolumns.music_modern_tracks]
            val musicList = ArrayList<Music>(trackRows.size)
            for (trackRow in trackRows) {
                val music = musicRows[trackRow.id]
                if (music == null) {
                    throw IllegalStateException("Music row not found: '${trackRow.internalName}'")
                }
                musicList += music
            }
            val mappedList = grouped.computeIfAbsent(area.id) { mutableListOf() }
            mappedList += musicList
        }
        return grouped
    }

    private fun loadClassicAreas(musicRows: Map<Int, Music>): Map<Int, Music> {
        val rows = dbTables[dbtables.music_classic]
        val areas = mutableMapOf<Int, Music>()
        for (row in rows) {
            val area = row[dbcolumns.music_classic_area]
            if (area.id in areas) {
                val message =
                    "Classic music area can only be mapped to a " +
                        "single track: '${area.internalName}' (row=$row)"
                throw IllegalStateException(message)
            }
            val trackRow = row[dbcolumns.music_classic_track]
            val music = musicRows[trackRow.id]
            if (music == null) {
                throw IllegalStateException("Music row not found: '${trackRow.internalName}'")
            }
            areas[area.id] = music
        }
        return areas
    }

    private fun unlockVarps(): List<VarpType> =
        listOf(
            music_varps.multi_1,
            music_varps.multi_2,
            music_varps.multi_3,
            music_varps.multi_4,
            music_varps.multi_5,
            music_varps.multi_6,
            music_varps.multi_7,
            music_varps.multi_8,
            music_varps.multi_9,
            music_varps.multi_10,
            music_varps.multi_11,
            music_varps.multi_12,
            music_varps.multi_13,
            music_varps.multi_14,
            music_varps.multi_15,
            music_varps.multi_16,
            music_varps.multi_17,
            music_varps.multi_18,
            music_varps.multi_19,
            music_varps.multi_20,
            music_varps.multi_21,
            music_varps.multi_22,
            music_varps.multi_23,
            music_varps.multi_24,
            music_varps.multi_25,
        )
}
