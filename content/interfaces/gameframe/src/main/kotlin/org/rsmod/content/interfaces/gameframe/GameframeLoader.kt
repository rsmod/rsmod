package org.rsmod.content.interfaces.gameframe

import jakarta.inject.Inject
import org.rsmod.content.interfaces.gameframe.config.gameframe_columns
import org.rsmod.content.interfaces.gameframe.config.gameframe_enums
import org.rsmod.game.dbtable.DbRow
import org.rsmod.game.dbtable.DbRowResolver
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.ui.Component

internal class GameframeLoader
@Inject
constructor(private val rows: DbRowResolver, private val enums: EnumTypeMapResolver) {
    fun loadGameframes(): Map<Int, Gameframe> {
        return loadGameframeRows()
    }

    private fun loadGameframeRows(): Map<Int, Gameframe> {
        val mapped = mutableMapOf<Int, Gameframe>()

        val list = enums[gameframe_enums.list].filterValuesNotNull()
        for (entry in list.values) {
            val gameframe = loadGameframe(rows[entry])
            val previous = mapped[gameframe.topLevel.id]
            if (previous != null) {
                val message =
                    "Gameframe for toplevel already exists: '${previous.topLevel.internalName}' " +
                        "(previous=$previous, curr=$gameframe)"
                throw IllegalStateException(message)
            }
            mapped[gameframe.topLevel.id] = gameframe
        }

        return mapped
    }

    private fun loadGameframe(row: DbRow): Gameframe {
        val topLevel = row[gameframe_columns.toplevel]
        val overlays = row[gameframe_columns.overlays]
        val clientMode = row[gameframe_columns.clientmode]
        val resizable = row[gameframe_columns.resizable]
        val isDefault = row[gameframe_columns.is_default]
        val stoneArrangement = row[gameframe_columns.stone_arrangement]
        val mappings = linkedMapOf<Component, Component>()

        val mappingsEnum = row[gameframe_columns.mappings]
        val mappingRedirects = enums[mappingsEnum].filterValuesNotNull()
        for ((base, translated) in mappingRedirects) {
            mappings[Component(base.packed)] = Component(translated.packed)
        }

        return Gameframe(
            topLevel = topLevel,
            overlays = overlays,
            mappings = mappings,
            clientMode = clientMode,
            resizable = resizable,
            isDefault = isDefault,
            stoneArrangement = stoneArrangement,
        )
    }

    fun loadMoveEvents(): Map<ComponentType, ComponentType> {
        val list = enums[gameframe_enums.move_events].filterValuesNotNull()
        return list.backing
    }
}
