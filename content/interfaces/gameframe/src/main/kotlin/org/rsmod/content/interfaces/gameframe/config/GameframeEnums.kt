@file:Suppress("unused")

package org.rsmod.content.interfaces.gameframe.config

import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.api.config.refs.components
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.dbrow.DbRowType

typealias gameframe_enums = GameframeEnums

object GameframeEnums : EnumReferences() {
    val toplevel = find<EnumComp, EnumComp>("fixed_pane_redirect", 4205535)
    val toplevel_osrs_stretch = find<EnumComp, EnumComp>("resizable_basic_pane_redirect", 4209256)
    val toplevel_pre_eoc = find<EnumComp, EnumComp>("side_panels_resizable_pane_redirect", 4212977)

    val list = find<Int, DbRowType>("gameframe_dbrows")
    val move_events = find<ComponentType, ComponentType>("toplevel_move_events")
}

object GameframeEnumBuilder : EnumBuilder() {
    init {
        buildAutoInt<DbRowType>("gameframe_dbrows") {
            this += gameframe_rows.toplevel
            this += gameframe_rows.osrs_stretch
            this += gameframe_rows.pre_eoc
        }

        build<ComponentType, ComponentType>("toplevel_move_events") {
            this[components.toplevel_target_side0] = gameframe_components.toplevel_stone0
            this[components.toplevel_target_side1] = gameframe_components.toplevel_stone1
            this[components.toplevel_target_side2] = gameframe_components.toplevel_stone2
            this[components.toplevel_target_side3] = gameframe_components.toplevel_stone3
            this[components.toplevel_target_side4] = gameframe_components.toplevel_stone4
            this[components.toplevel_target_side5] = gameframe_components.toplevel_stone5
            this[components.toplevel_target_side6] = gameframe_components.toplevel_stone6
            this[components.toplevel_target_side7] = gameframe_components.toplevel_stone7
            this[components.toplevel_target_side8] = gameframe_components.toplevel_stone8
            this[components.toplevel_target_side9] = gameframe_components.toplevel_stone9
            this[components.toplevel_target_side10] = gameframe_components.toplevel_stone10
            this[components.toplevel_target_side11] = gameframe_components.toplevel_stone11
            this[components.toplevel_target_side12] = gameframe_components.toplevel_stone12
            this[components.toplevel_target_side13] = gameframe_components.toplevel_stone13
        }
    }
}
