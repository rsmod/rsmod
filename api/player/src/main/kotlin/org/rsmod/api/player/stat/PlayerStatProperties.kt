package org.rsmod.api.player.stat

import org.rsmod.api.config.refs.stats
import org.rsmod.game.entity.Player

public val Player.attackLvl
    get() = statMap.getCurrentLevel(stats.attack)

public val Player.baseAttackLvl
    get() = statMap.getBaseLevel(stats.attack)

public val Player.defenceLvl
    get() = statMap.getCurrentLevel(stats.defence)

public val Player.baseDefenceLvl
    get() = statMap.getBaseLevel(stats.defence)

public val Player.strengthLvl
    get() = statMap.getCurrentLevel(stats.strength)

public val Player.baseStrengthLvl
    get() = statMap.getBaseLevel(stats.strength)

public val Player.hitpointsLvl
    get() = statMap.getCurrentLevel(stats.hitpoints)

public val Player.baseHitpointsLvl
    get() = statMap.getBaseLevel(stats.hitpoints)

public val Player.rangedLvl
    get() = statMap.getCurrentLevel(stats.ranged)

public val Player.baseRangedLvl
    get() = statMap.getBaseLevel(stats.ranged)

public val Player.prayerLvl
    get() = statMap.getCurrentLevel(stats.prayer)

public val Player.basePrayerLvl
    get() = statMap.getBaseLevel(stats.prayer)

public val Player.magicLvl
    get() = statMap.getCurrentLevel(stats.magic)

public val Player.baseMagicLvl
    get() = statMap.getBaseLevel(stats.magic)

public val Player.cookingLvl
    get() = statMap.getCurrentLevel(stats.cooking)

public val Player.baseCookingLvl
    get() = statMap.getBaseLevel(stats.cooking)

public val Player.woodcuttingLvl
    get() = statMap.getCurrentLevel(stats.woodcutting)

public val Player.baseWoodcuttingLvl
    get() = statMap.getBaseLevel(stats.woodcutting)

public val Player.fletchingLvl
    get() = statMap.getCurrentLevel(stats.fletching)

public val Player.baseFletchingLvl
    get() = statMap.getBaseLevel(stats.fletching)

public val Player.fishingLvl
    get() = statMap.getCurrentLevel(stats.fishing)

public val Player.baseFishingLvl
    get() = statMap.getBaseLevel(stats.fishing)

public val Player.firemakingLvl
    get() = statMap.getCurrentLevel(stats.firemaking)

public val Player.baseFiremakingLvl
    get() = statMap.getBaseLevel(stats.firemaking)

public val Player.craftingLvl
    get() = statMap.getCurrentLevel(stats.crafting)

public val Player.baseCraftingLvl
    get() = statMap.getBaseLevel(stats.crafting)

public val Player.smithingLvl
    get() = statMap.getCurrentLevel(stats.smithing)

public val Player.baseSmithingLvl
    get() = statMap.getBaseLevel(stats.smithing)

public val Player.miningLvl
    get() = statMap.getCurrentLevel(stats.mining)

public val Player.baseMiningLvl
    get() = statMap.getBaseLevel(stats.mining)

public val Player.herbloreLvl
    get() = statMap.getCurrentLevel(stats.herblore)

public val Player.baseHerbloreLvl
    get() = statMap.getBaseLevel(stats.herblore)

public val Player.agilityLvl
    get() = statMap.getCurrentLevel(stats.agility)

public val Player.baseAgilityLvl
    get() = statMap.getBaseLevel(stats.agility)

public val Player.thievingLvl
    get() = statMap.getCurrentLevel(stats.thieving)

public val Player.baseThievingLvl
    get() = statMap.getBaseLevel(stats.thieving)

public val Player.slayerLvl
    get() = statMap.getCurrentLevel(stats.slayer)

public val Player.baseSlayerLvl
    get() = statMap.getBaseLevel(stats.slayer)

public val Player.farmingLvl
    get() = statMap.getCurrentLevel(stats.farming)

public val Player.baseFarmingLvl
    get() = statMap.getBaseLevel(stats.farming)

public val Player.runecraftingLvl
    get() = statMap.getCurrentLevel(stats.runecrafting)

public val Player.baseRunecraftingLvl
    get() = statMap.getBaseLevel(stats.runecrafting)

public val Player.hunterLvl
    get() = statMap.getCurrentLevel(stats.hunter)

public val Player.baseHunterLvl
    get() = statMap.getBaseLevel(stats.hunter)

public val Player.constructionLvl
    get() = statMap.getCurrentLevel(stats.construction)

public val Player.baseConstructionLvl
    get() = statMap.getBaseLevel(stats.construction)
