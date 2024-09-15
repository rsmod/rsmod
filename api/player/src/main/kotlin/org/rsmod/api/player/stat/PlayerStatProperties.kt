package org.rsmod.api.player.stat

import org.rsmod.api.config.refs.stats
import org.rsmod.game.stat.PlayerStatMap

public val PlayerStatMap.attackLvl
    get() = getCurrentLevel(stats.attack)

public val PlayerStatMap.baseAttackLvl
    get() = getBaseLevel(stats.attack)

public val PlayerStatMap.defenceLvl
    get() = getCurrentLevel(stats.defence)

public val PlayerStatMap.baseDefenceLvl
    get() = getBaseLevel(stats.defence)

public val PlayerStatMap.strengthLvl
    get() = getCurrentLevel(stats.strength)

public val PlayerStatMap.baseStrengthLvl
    get() = getBaseLevel(stats.strength)

public val PlayerStatMap.hitpointsLvl
    get() = getCurrentLevel(stats.hitpoints)

public val PlayerStatMap.baseHitpointsLvl
    get() = getBaseLevel(stats.hitpoints)

public val PlayerStatMap.rangedLvl
    get() = getCurrentLevel(stats.ranged)

public val PlayerStatMap.baseRangedLvl
    get() = getBaseLevel(stats.ranged)

public val PlayerStatMap.prayerLvl
    get() = getCurrentLevel(stats.prayer)

public val PlayerStatMap.basePrayerLvl
    get() = getBaseLevel(stats.prayer)

public val PlayerStatMap.magicLvl
    get() = getCurrentLevel(stats.magic)

public val PlayerStatMap.baseMagicLvl
    get() = getBaseLevel(stats.magic)

public val PlayerStatMap.cookingLvl
    get() = getCurrentLevel(stats.cooking)

public val PlayerStatMap.baseCookingLvl
    get() = getBaseLevel(stats.cooking)

public val PlayerStatMap.woodcuttingLvl
    get() = getCurrentLevel(stats.woodcutting)

public val PlayerStatMap.baseWoodcuttingLvl
    get() = getBaseLevel(stats.woodcutting)

public val PlayerStatMap.fletchingLvl
    get() = getCurrentLevel(stats.fletching)

public val PlayerStatMap.baseFletchingLvl
    get() = getBaseLevel(stats.fletching)

public val PlayerStatMap.fishingLvl
    get() = getCurrentLevel(stats.fishing)

public val PlayerStatMap.baseFishingLvl
    get() = getBaseLevel(stats.fishing)

public val PlayerStatMap.firemakingLvl
    get() = getCurrentLevel(stats.firemaking)

public val PlayerStatMap.baseFiremakingLvl
    get() = getBaseLevel(stats.firemaking)

public val PlayerStatMap.craftingLvl
    get() = getCurrentLevel(stats.crafting)

public val PlayerStatMap.baseCraftingLvl
    get() = getBaseLevel(stats.crafting)

public val PlayerStatMap.smithingLvl
    get() = getCurrentLevel(stats.smithing)

public val PlayerStatMap.baseSmithingLvl
    get() = getBaseLevel(stats.smithing)

public val PlayerStatMap.miningLvl
    get() = getCurrentLevel(stats.mining)

public val PlayerStatMap.baseMiningLvl
    get() = getBaseLevel(stats.mining)

public val PlayerStatMap.herbloreLvl
    get() = getCurrentLevel(stats.herblore)

public val PlayerStatMap.baseHerbloreLvl
    get() = getBaseLevel(stats.herblore)

public val PlayerStatMap.agilityLvl
    get() = getCurrentLevel(stats.agility)

public val PlayerStatMap.baseAgilityLvl
    get() = getBaseLevel(stats.agility)

public val PlayerStatMap.thievingLvl
    get() = getCurrentLevel(stats.thieving)

public val PlayerStatMap.baseThievingLvl
    get() = getBaseLevel(stats.thieving)

public val PlayerStatMap.slayerLvl
    get() = getCurrentLevel(stats.slayer)

public val PlayerStatMap.baseSlayerLvl
    get() = getBaseLevel(stats.slayer)

public val PlayerStatMap.farmingLvl
    get() = getCurrentLevel(stats.farming)

public val PlayerStatMap.baseFarmingLvl
    get() = getBaseLevel(stats.farming)

public val PlayerStatMap.runecraftingLvl
    get() = getCurrentLevel(stats.runecrafting)

public val PlayerStatMap.baseRunecraftingLvl
    get() = getBaseLevel(stats.runecrafting)

public val PlayerStatMap.hunterLvl
    get() = getCurrentLevel(stats.hunter)

public val PlayerStatMap.baseHunterLvl
    get() = getBaseLevel(stats.hunter)

public val PlayerStatMap.constructionLvl
    get() = getCurrentLevel(stats.construction)

public val PlayerStatMap.baseConstructionLvl
    get() = getBaseLevel(stats.construction)
