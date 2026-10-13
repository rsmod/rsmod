package org.runerealm.api.drop.table

import org.runerealm.game.type.npc.NpcType

/**
 * Content modules extend this class to register npc drop tables, and bind it through
 * `addSetBinding<NpcDropTableProvider>(...)` in their plugin module.
 */
public abstract class NpcDropTableProvider {
    private val tables = mutableMapOf<Int, NpcDropTable>()

    internal val registered: Map<Int, NpcDropTable>
        get() = tables

    protected fun register(table: NpcDropTable, vararg npcs: NpcType) {
        for (npc in npcs) {
            check(npc.id !in tables) { "Drop table already registered for npc: $npc" }
            tables[npc.id] = table
        }
    }
}
