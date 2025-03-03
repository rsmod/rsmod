package org.rsmod.api.player.stat

import org.rsmod.api.config.refs.stats
import org.rsmod.game.entity.Player

public val Player.attackLvl: Int
    get() = statMap.getCurrentLevel(stats.attack).toInt()

public val Player.baseAttackLvl: Int
    get() = statMap.getBaseLevel(stats.attack).toInt()

public val Player.defenceLvl: Int
    get() = statMap.getCurrentLevel(stats.defence).toInt()

public val Player.baseDefenceLvl: Int
    get() = statMap.getBaseLevel(stats.defence).toInt()

public val Player.strengthLvl: Int
    get() = statMap.getCurrentLevel(stats.strength).toInt()

public val Player.baseStrengthLvl: Int
    get() = statMap.getBaseLevel(stats.strength).toInt()

public val Player.hitpoints: Int
    get() = statMap.getCurrentLevel(stats.hitpoints).toInt()

public val Player.baseHitpointsLvl: Int
    get() = statMap.getBaseLevel(stats.hitpoints).toInt()

public val Player.rangedLvl: Int
    get() = statMap.getCurrentLevel(stats.ranged).toInt()

public val Player.baseRangedLvl: Int
    get() = statMap.getBaseLevel(stats.ranged).toInt()

public val Player.prayerLvl: Int
    get() = statMap.getCurrentLevel(stats.prayer).toInt()

public val Player.basePrayerLvl: Int
    get() = statMap.getBaseLevel(stats.prayer).toInt()

public val Player.magicLvl: Int
    get() = statMap.getCurrentLevel(stats.magic).toInt()

public val Player.baseMagicLvl: Int
    get() = statMap.getBaseLevel(stats.magic).toInt()

public val Player.cookingLvl: Int
    get() = statMap.getCurrentLevel(stats.cooking).toInt()

public val Player.baseCookingLvl: Int
    get() = statMap.getBaseLevel(stats.cooking).toInt()

public val Player.woodcuttingLvl: Int
    get() = statMap.getCurrentLevel(stats.woodcutting).toInt()

public val Player.baseWoodcuttingLvl: Int
    get() = statMap.getBaseLevel(stats.woodcutting).toInt()

public val Player.fletchingLvl: Int
    get() = statMap.getCurrentLevel(stats.fletching).toInt()

public val Player.baseFletchingLvl: Int
    get() = statMap.getBaseLevel(stats.fletching).toInt()

public val Player.fishingLvl: Int
    get() = statMap.getCurrentLevel(stats.fishing).toInt()

public val Player.baseFishingLvl: Int
    get() = statMap.getBaseLevel(stats.fishing).toInt()

public val Player.firemakingLvl: Int
    get() = statMap.getCurrentLevel(stats.firemaking).toInt()

public val Player.baseFiremakingLvl: Int
    get() = statMap.getBaseLevel(stats.firemaking).toInt()

public val Player.craftingLvl: Int
    get() = statMap.getCurrentLevel(stats.crafting).toInt()

public val Player.baseCraftingLvl: Int
    get() = statMap.getBaseLevel(stats.crafting).toInt()

public val Player.smithingLvl: Int
    get() = statMap.getCurrentLevel(stats.smithing).toInt()

public val Player.baseSmithingLvl: Int
    get() = statMap.getBaseLevel(stats.smithing).toInt()

public val Player.miningLvl: Int
    get() = statMap.getCurrentLevel(stats.mining).toInt()

public val Player.baseMiningLvl: Int
    get() = statMap.getBaseLevel(stats.mining).toInt()

public val Player.herbloreLvl: Int
    get() = statMap.getCurrentLevel(stats.herblore).toInt()

public val Player.baseHerbloreLvl: Int
    get() = statMap.getBaseLevel(stats.herblore).toInt()

public val Player.agilityLvl: Int
    get() = statMap.getCurrentLevel(stats.agility).toInt()

public val Player.baseAgilityLvl: Int
    get() = statMap.getBaseLevel(stats.agility).toInt()

public val Player.thievingLvl: Int
    get() = statMap.getCurrentLevel(stats.thieving).toInt()

public val Player.baseThievingLvl: Int
    get() = statMap.getBaseLevel(stats.thieving).toInt()

public val Player.slayerLvl: Int
    get() = statMap.getCurrentLevel(stats.slayer).toInt()

public val Player.baseSlayerLvl: Int
    get() = statMap.getBaseLevel(stats.slayer).toInt()

public val Player.farmingLvl: Int
    get() = statMap.getCurrentLevel(stats.farming).toInt()

public val Player.baseFarmingLvl: Int
    get() = statMap.getBaseLevel(stats.farming).toInt()

public val Player.runecraftingLvl: Int
    get() = statMap.getCurrentLevel(stats.runecrafting).toInt()

public val Player.baseRunecraftingLvl: Int
    get() = statMap.getBaseLevel(stats.runecrafting).toInt()

public val Player.hunterLvl: Int
    get() = statMap.getCurrentLevel(stats.hunter).toInt()

public val Player.baseHunterLvl: Int
    get() = statMap.getBaseLevel(stats.hunter).toInt()

public val Player.constructionLvl: Int
    get() = statMap.getCurrentLevel(stats.construction).toInt()

public val Player.baseConstructionLvl: Int
    get() = statMap.getBaseLevel(stats.construction).toInt()
