package org.runerealm.api.drop.table

import org.runerealm.game.type.obj.ObjType

public fun npcDropTable(init: NpcDropTableBuilder.() -> Unit): NpcDropTable =
    NpcDropTableBuilder().apply(init).build()

public fun weightedDropTable(
    denominator: Int,
    init: WeightedDropTableBuilder.() -> Unit,
): WeightedDropTable = WeightedDropTableBuilder(denominator).apply(init).build()

@DslMarker private annotation class DropTableDsl

@DropTableDsl
public class NpcDropTableBuilder {
    private val always = mutableListOf<ObjDrop>()
    private var main: WeightedDropTable? = null
    private val tertiary = mutableListOf<TertiaryDrop>()

    public fun always(obj: ObjType, min: Int = 1, max: Int = min) {
        always += ObjDrop(obj, min, max)
    }

    public fun main(denominator: Int, init: WeightedDropTableBuilder.() -> Unit) {
        check(main == null) { "Main table has already been defined." }
        main = weightedDropTable(denominator, init)
    }

    public fun tertiary(
        numerator: Int,
        denominator: Int,
        obj: ObjType,
        min: Int = 1,
        max: Int = min,
    ) {
        tertiary += TertiaryDrop(numerator, denominator, ObjDrop(obj, min, max))
    }

    public fun build(): NpcDropTable = NpcDropTable(always.toList(), main, tertiary.toList())
}

@DropTableDsl
public class WeightedDropTableBuilder(private val denominator: Int) {
    private val entries = mutableListOf<WeightedDrop>()

    public fun obj(weight: Int, obj: ObjType, min: Int = 1, max: Int = min) {
        entries += WeightedDrop(weight, DropResult.Obj(ObjDrop(obj, min, max)))
    }

    public fun nothing(weight: Int) {
        entries += WeightedDrop(weight, DropResult.Nothing)
    }

    public fun table(weight: Int, table: WeightedDropTable) {
        entries += WeightedDrop(weight, DropResult.SubTable(table))
    }

    public fun build(): WeightedDropTable = WeightedDropTable(denominator, entries.toList())
}
