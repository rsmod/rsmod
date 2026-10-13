package org.runerealm.api.drop.table

import jakarta.inject.Inject
import org.runerealm.api.random.GameRandom
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.obj.ObjType

public class NpcDropTables @Inject constructor(providers: Set<NpcDropTableProvider>) {
    private val tables: Map<Int, NpcDropTable> = buildMap {
        for (provider in providers) {
            for ((npc, table) in provider.registered) {
                check(npc !in this) { "Drop table registered more than once for npc id: $npc" }
                put(npc, table)
            }
        }
    }

    public operator fun get(npc: NpcType): NpcDropTable? = tables[npc.id]

    public operator fun contains(npc: NpcType): Boolean = npc.id in tables

    /** Rolls [table], returning each obj (and its quantity) that should be dropped. */
    public fun roll(table: NpcDropTable, random: GameRandom): List<RolledDrop> {
        val drops = mutableListOf<RolledDrop>()
        for (drop in table.always) {
            drops += drop.roll(random)
        }
        table.main?.let { main -> main.roll(random)?.let(drops::add) }
        for (tertiary in table.tertiary) {
            if (random.of(tertiary.denominator) < tertiary.numerator) {
                drops += tertiary.drop.roll(random)
            }
        }
        return drops
    }

    private fun WeightedDropTable.roll(random: GameRandom): RolledDrop? {
        var roll = random.of(denominator)
        for (entry in entries) {
            if (roll < entry.weight) {
                return when (val result = entry.result) {
                    DropResult.Nothing -> null
                    is DropResult.Obj -> result.drop.roll(random)
                    is DropResult.SubTable -> result.table.roll(random)
                }
            }
            roll -= entry.weight
        }
        error("Weighted roll out of bounds: denominator=$denominator")
    }

    private fun ObjDrop.roll(random: GameRandom): RolledDrop {
        val count = if (min == max) min else random.of(min, max)
        return RolledDrop(obj, count)
    }
}

public data class RolledDrop(val obj: ObjType, val count: Int)
