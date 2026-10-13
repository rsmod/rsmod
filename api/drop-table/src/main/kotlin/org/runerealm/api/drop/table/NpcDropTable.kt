package org.runerealm.api.drop.table

import org.runerealm.game.type.obj.ObjType

/**
 * A npc's drop table, following the structure used by the OSRS Wiki:
 * - [always]: drops given on every kill ("100%").
 * - [main]: a single weighted roll, which may land on "Nothing" or a shared sub-table.
 * - [tertiary]: drops rolled independently of the [main] table.
 */
public class NpcDropTable(
    public val always: List<ObjDrop>,
    public val main: WeightedDropTable?,
    public val tertiary: List<TertiaryDrop>,
) {
    public companion object {
        /** A table for npcs that drop nothing at all. */
        public val Empty: NpcDropTable = NpcDropTable(emptyList(), null, emptyList())
    }
}

/** An obj drop with an inclusive quantity range. */
public data class ObjDrop(val obj: ObjType, val min: Int, val max: Int = min) {
    init {
        require(min in 1..max) { "Invalid quantity range: $min..$max ($obj)" }
    }
}

/**
 * A table where a single [WeightedDrop] is selected, with each entry having a `weight` chance out
 * of [denominator]. The weights must add up to exactly [denominator].
 */
public class WeightedDropTable(
    public val denominator: Int,
    public val entries: List<WeightedDrop>,
) {
    init {
        val total = entries.sumOf { it.weight }
        require(total == denominator) {
            "Weights must add up to the denominator: total=$total, denominator=$denominator"
        }
    }
}

public data class WeightedDrop(val weight: Int, val result: DropResult) {
    init {
        require(weight > 0) { "Weight must be positive: $weight" }
    }
}

public sealed interface DropResult {
    public data object Nothing : DropResult

    public data class Obj(val drop: ObjDrop) : DropResult

    public data class SubTable(val table: WeightedDropTable) : DropResult
}

/** A drop rolled independently, with a [numerator] in [denominator] chance. */
public data class TertiaryDrop(val numerator: Int, val denominator: Int, val drop: ObjDrop) {
    init {
        require(numerator in 1..denominator) { "Invalid chance: $numerator/$denominator" }
    }
}
