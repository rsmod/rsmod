package org.rsmod.content.interfaces.gameframe.config

import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.type.builders.dbrow.DbRowBuilder
import org.rsmod.api.type.builders.dbtable.DbTableBuilder
import org.rsmod.api.type.refs.dbcol.DbColumnReferences
import org.rsmod.api.type.refs.dbrow.DbRowReferences
import org.rsmod.api.type.refs.dbtable.DbTableReferences
import org.rsmod.content.interfaces.gameframe.GameframeOverlay
import org.rsmod.content.interfaces.gameframe.StandardOverlays
import org.rsmod.game.dbtable.DbColumnCodec
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.literal.CacheVarLiteral

typealias gameframe_columns = GameframeDbColumns

typealias gameframe_rows = GameframeDbRows

typealias gameframe_tables = GameframeDbTables

object GameframeDbColumns : DbColumnReferences() {
    val toplevel = interf("gameframe:toplevel")
    val mappings = enum("gameframe:mappings", ComponentType::class, ComponentType::class)
    val overlays = list("gameframe:open_overlays", GameframeOverlayCodec)
    val clientmode = int("gameframe:clientmode")
    val resizable = boolean("gameframe:resizable")
    val is_default = boolean("gameframe:is_default")
    val stone_arrangement = boolean("gameframe:stone_arrangement")
}

object GameframeDbRows : DbRowReferences() {
    val toplevel = find("gameframe_toplevel")
    val osrs_stretch = find("gameframe_osrs_stretch")
    val pre_eoc = find("gameframe_pre_eoc")
}

object GameframeDbTables : DbTableReferences() {
    val gameframe = find("gameframe")
}

object GameframeDbRowBuilder : DbRowBuilder() {
    init {
        build("gameframe_toplevel") {
            table = gameframe_tables.gameframe
            column(gameframe_columns.toplevel) { value = interfaces.toplevel }
            column(gameframe_columns.mappings) { value = gameframe_enums.toplevel }
            column(gameframe_columns.clientmode) { value = 0 }
            column(gameframe_columns.resizable) { value = false }
            column(gameframe_columns.is_default) { value = true }
        }

        build("gameframe_osrs_stretch") {
            table = gameframe_tables.gameframe
            column(gameframe_columns.toplevel) { value = interfaces.toplevel_osrs_stretch }
            column(gameframe_columns.mappings) { value = gameframe_enums.toplevel_osrs_stretch }
            column(gameframe_columns.clientmode) { value = 1 }
        }

        build("gameframe_pre_eoc") {
            table = gameframe_tables.gameframe
            column(gameframe_columns.toplevel) { value = interfaces.toplevel_pre_eoc }
            column(gameframe_columns.mappings) { value = gameframe_enums.toplevel_pre_eoc }
            column(gameframe_columns.clientmode) { value = 2 }
            column(gameframe_columns.stone_arrangement) { value = true }
        }
    }
}

object GameframeDbTableBuilder : DbTableBuilder() {
    init {
        build("gameframe") {
            column(gameframe_columns.toplevel)
            column(gameframe_columns.mappings)
            columnList(gameframe_columns.overlays) { default = StandardOverlays.open }
            column(gameframe_columns.clientmode)
            column(gameframe_columns.resizable) { default = true }
            column(gameframe_columns.is_default) { default = false }
            column(gameframe_columns.stone_arrangement) { default = false }
        }
    }
}

private object GameframeOverlayCodec : DbColumnCodec<Int, GameframeOverlay> {
    override val types: List<CacheVarLiteral>
        get() = listOf(CacheVarLiteral.INTERFACE, CacheVarLiteral.COMPONENT)

    override fun decode(
        iterator: DbColumnCodec.Iterator<Int, GameframeOverlay>,
        types: TypeListMap,
    ): GameframeOverlay {
        val interf = types.interfaces.getValue(iterator.next())
        val target = types.components.getValue(iterator.next())
        return GameframeOverlay(interf.toHashedType(), target.toHashedType())
    }

    override fun encode(value: GameframeOverlay): List<Int> {
        return listOf(value.interf.id, value.target.packed)
    }
}
