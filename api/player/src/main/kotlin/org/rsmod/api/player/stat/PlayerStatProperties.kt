package org.rsmod.api.player.stat

import org.rsmod.api.config.refs.stats
import org.rsmod.game.stat.PlayerStatMap

public val PlayerStatMap.attack
    get() = getCurrentLevel(stats.attack)

public val PlayerStatMap.baseAttack
    get() = getBaseLevel(stats.attack)

public val PlayerStatMap.defence
    get() = getCurrentLevel(stats.defence)

public val PlayerStatMap.baseDefence
    get() = getBaseLevel(stats.defence)

public val PlayerStatMap.strength
    get() = getCurrentLevel(stats.strength)

public val PlayerStatMap.baseStrength
    get() = getBaseLevel(stats.strength)

public val PlayerStatMap.hitpoints
    get() = getCurrentLevel(stats.hitpoints)

public val PlayerStatMap.baseHitpoints
    get() = getBaseLevel(stats.hitpoints)

public val PlayerStatMap.ranged
    get() = getCurrentLevel(stats.ranged)

public val PlayerStatMap.baseRanged
    get() = getBaseLevel(stats.ranged)

public val PlayerStatMap.prayer
    get() = getCurrentLevel(stats.prayer)

public val PlayerStatMap.basePrayer
    get() = getBaseLevel(stats.prayer)

public val PlayerStatMap.magic
    get() = getCurrentLevel(stats.magic)

public val PlayerStatMap.baseMagic
    get() = getBaseLevel(stats.magic)

public val PlayerStatMap.cooking
    get() = getCurrentLevel(stats.cooking)

public val PlayerStatMap.baseCooking
    get() = getBaseLevel(stats.cooking)

public val PlayerStatMap.woodcutting
    get() = getCurrentLevel(stats.woodcutting)

public val PlayerStatMap.baseWoodcutting
    get() = getBaseLevel(stats.woodcutting)

public val PlayerStatMap.fletching
    get() = getCurrentLevel(stats.fletching)

public val PlayerStatMap.baseFletching
    get() = getBaseLevel(stats.fletching)

public val PlayerStatMap.fishing
    get() = getCurrentLevel(stats.fishing)

public val PlayerStatMap.baseFishing
    get() = getBaseLevel(stats.fishing)

public val PlayerStatMap.firemaking
    get() = getCurrentLevel(stats.firemaking)

public val PlayerStatMap.baseFiremaking
    get() = getBaseLevel(stats.firemaking)

public val PlayerStatMap.crafting
    get() = getCurrentLevel(stats.crafting)

public val PlayerStatMap.baseCrafting
    get() = getBaseLevel(stats.crafting)

public val PlayerStatMap.smithing
    get() = getCurrentLevel(stats.smithing)

public val PlayerStatMap.baseSmithing
    get() = getBaseLevel(stats.smithing)

public val PlayerStatMap.mining
    get() = getCurrentLevel(stats.mining)

public val PlayerStatMap.baseMining
    get() = getBaseLevel(stats.mining)

public val PlayerStatMap.herblore
    get() = getCurrentLevel(stats.herblore)

public val PlayerStatMap.baseHerblore
    get() = getBaseLevel(stats.herblore)

public val PlayerStatMap.agility
    get() = getCurrentLevel(stats.agility)

public val PlayerStatMap.baseAgility
    get() = getBaseLevel(stats.agility)

public val PlayerStatMap.thieving
    get() = getCurrentLevel(stats.thieving)

public val PlayerStatMap.baseThieving
    get() = getBaseLevel(stats.thieving)

public val PlayerStatMap.slayer
    get() = getCurrentLevel(stats.slayer)

public val PlayerStatMap.baseSlayer
    get() = getBaseLevel(stats.slayer)

public val PlayerStatMap.farming
    get() = getCurrentLevel(stats.farming)

public val PlayerStatMap.baseFarming
    get() = getBaseLevel(stats.farming)

public val PlayerStatMap.runecrafting
    get() = getCurrentLevel(stats.runecrafting)

public val PlayerStatMap.baseRunecrafting
    get() = getBaseLevel(stats.runecrafting)

public val PlayerStatMap.hunter
    get() = getCurrentLevel(stats.hunter)

public val PlayerStatMap.baseHunter
    get() = getBaseLevel(stats.hunter)

public val PlayerStatMap.construction
    get() = getCurrentLevel(stats.construction)

public val PlayerStatMap.baseConstruction
    get() = getBaseLevel(stats.construction)
