package org.rsmod.plugins.api.model.stat

import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatMap

val StatMap.attack: Stat
    get() = getValue(Stats.Attack)

val StatMap.defence: Stat
    get() = getValue(Stats.Defence)

val StatMap.strength: Stat
    get() = getValue(Stats.Strength)

val StatMap.hitpoints: Stat
    get() = getValue(Stats.Hitpoints)

val StatMap.ranged: Stat
    get() = getValue(Stats.Ranged)

val StatMap.prayer: Stat
    get() = getValue(Stats.Prayer)

val StatMap.magic: Stat
    get() = getValue(Stats.Magic)

val StatMap.cooking: Stat
    get() = getValue(Stats.Cooking)

val StatMap.woodcutting: Stat
    get() = getValue(Stats.Woodcutting)

val StatMap.fletching: Stat
    get() = getValue(Stats.Fletching)

val StatMap.fishing: Stat
    get() = getValue(Stats.Fishing)

val StatMap.firemaking: Stat
    get() = getValue(Stats.Firemaking)

val StatMap.crafting: Stat
    get() = getValue(Stats.Crafting)

val StatMap.smithing: Stat
    get() = getValue(Stats.Smithing)

val StatMap.mining: Stat
    get() = getValue(Stats.Mining)

val StatMap.herblore: Stat
    get() = getValue(Stats.Herblore)

val StatMap.agility: Stat
    get() = getValue(Stats.Agility)

val StatMap.thieving: Stat
    get() = getValue(Stats.Thieving)

val StatMap.slayer: Stat
    get() = getValue(Stats.Slayer)

val StatMap.farming: Stat
    get() = getValue(Stats.Farming)

val StatMap.runecrafting: Stat
    get() = getValue(Stats.Runecrafting)

val StatMap.hunter: Stat
    get() = getValue(Stats.Hunter)

val StatMap.construction: Stat
    get() = getValue(Stats.Construction)

fun StatMap.combatLevel(): Int {
    val melee = attack.baseLevel() + strength.baseLevel()
    val ranged = (ranged.baseLevel() * 3) / 2
    val magic = (magic.baseLevel() * 3) / 2
    val highest = melee.coerceAtLeast(ranged).coerceAtLeast(magic)
    val defence = (defence.baseLevel() + hitpoints.baseLevel()) + (prayer.baseLevel() / 2)
    return ((highest * 13) / 10 + defence) / 4
}
