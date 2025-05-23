package org.rsmod.api.spells.runes.unlimited

import org.rsmod.api.spells.runes.unlimited.configs.unlimited_enums
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.ObjType

public class UnlimitedRuneRepository {
    // Magic rune validation has a subtle distinction involving certain "unlimited source" objs
    // (e.g., elemental staves): some are checked before the `fake_runes_enabled` condition, and
    // others after. While niche, deviating from the original order would cause behavioral
    // differences. To preserve this, we categorize the pre-check sources as "high-priority" and
    // post-check ones as "low-priority."
    private lateinit var highPriority: Map<Int, Set<Int>>
    private lateinit var lowPriority: Map<Int, Set<Int>>

    public fun isHighPrioritySource(rune: ObjType, righthand: InvObj?, lefthand: InvObj?): Boolean {
        val sources = highPriority[rune.id] ?: return false
        return righthand?.id in sources || lefthand?.id in sources
    }

    public fun isLowPrioritySource(rune: ObjType, righthand: InvObj?, lefthand: InvObj?): Boolean {
        val sources = lowPriority[rune.id] ?: return false
        return righthand?.id in sources || lefthand?.id in sources
    }

    public fun isSource(rune: ObjType, righthand: InvObj?, lefthand: InvObj?): Boolean =
        isHighPrioritySource(rune, righthand, lefthand) ||
            isLowPrioritySource(rune, righthand, lefthand)

    internal fun init(highPriority: Map<Int, Set<Int>>, lowPriority: Map<Int, Set<Int>>) {
        this.highPriority = highPriority
        this.lowPriority = lowPriority
    }

    internal fun init(resolver: EnumTypeMapResolver) {
        val highPriority = loadHighPriority(resolver)
        val lowPriority = loadLowPriority(resolver)
        init(highPriority, lowPriority)
    }

    private fun loadHighPriority(resolver: EnumTypeMapResolver): Map<Int, Set<Int>> {
        val mapped = hashMapOf<Int, MutableSet<Int>>()

        val affinityStaffEnum = resolver[unlimited_enums.rune_staves].filterValuesNotNull()
        for ((rune, staffEnum) in affinityStaffEnum) {
            val staffList = resolver[staffEnum].filterValuesNotNull().filter { it.value }
            val targetSet = mapped.getOrPut(rune.id) { mutableSetOf() }
            targetSet += staffList.map { it.key.id }
        }

        val unlimitedSourceEnum = resolver[unlimited_enums.high_priority].filterValuesNotNull()
        for ((rune, sourceListEnum) in unlimitedSourceEnum) {
            val sources = resolver[sourceListEnum].filterValuesNotNull().values
            val targetSet = mapped.getOrPut(rune.id) { mutableSetOf() }
            targetSet += sources.map { it.id }
        }

        return mapped
    }

    private fun loadLowPriority(resolver: EnumTypeMapResolver): Map<Int, Set<Int>> {
        val mappedUnlimited = hashMapOf<Int, Set<Int>>()

        val unlimitedSourceEnum = resolver[unlimited_enums.low_priority].filterValuesNotNull()
        for ((rune, sourceListEnum) in unlimitedSourceEnum) {
            val sources = resolver[sourceListEnum].filterValuesNotNull().values
            mappedUnlimited[rune.id] = sources.map { it.id }.toHashSet()
        }

        return mappedUnlimited
    }
}
