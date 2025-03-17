package org.rsmod.api.player.stat

import org.rsmod.api.config.refs.stats
import org.rsmod.game.entity.Player

public val Player.attackLvl: Int
    get() = stat(stats.attack)

public val Player.baseAttackLvl: Int
    get() = statBase(stats.attack)

public val Player.defenceLvl: Int
    get() = stat(stats.defence)

public val Player.baseDefenceLvl: Int
    get() = statBase(stats.defence)

public val Player.strengthLvl: Int
    get() = stat(stats.strength)

public val Player.baseStrengthLvl: Int
    get() = statBase(stats.strength)

public val Player.hitpoints: Int
    get() = stat(stats.hitpoints)

public val Player.baseHitpointsLvl: Int
    get() = statBase(stats.hitpoints)

public val Player.rangedLvl: Int
    get() = stat(stats.ranged)

public val Player.baseRangedLvl: Int
    get() = statBase(stats.ranged)

public val Player.prayerLvl: Int
    get() = stat(stats.prayer)

public val Player.basePrayerLvl: Int
    get() = statBase(stats.prayer)

public val Player.magicLvl: Int
    get() = stat(stats.magic)

public val Player.baseMagicLvl: Int
    get() = statBase(stats.magic)

public val Player.cookingLvl: Int
    get() = stat(stats.cooking)

public val Player.baseCookingLvl: Int
    get() = statBase(stats.cooking)

public val Player.woodcuttingLvl: Int
    get() = stat(stats.woodcutting)

public val Player.baseWoodcuttingLvl: Int
    get() = statBase(stats.woodcutting)

public val Player.fletchingLvl: Int
    get() = stat(stats.fletching)

public val Player.baseFletchingLvl: Int
    get() = statBase(stats.fletching)

public val Player.fishingLvl: Int
    get() = stat(stats.fishing)

public val Player.baseFishingLvl: Int
    get() = statBase(stats.fishing)

public val Player.firemakingLvl: Int
    get() = stat(stats.firemaking)

public val Player.baseFiremakingLvl: Int
    get() = statBase(stats.firemaking)

public val Player.craftingLvl: Int
    get() = stat(stats.crafting)

public val Player.baseCraftingLvl: Int
    get() = statBase(stats.crafting)

public val Player.smithingLvl: Int
    get() = stat(stats.smithing)

public val Player.baseSmithingLvl: Int
    get() = statBase(stats.smithing)

public val Player.miningLvl: Int
    get() = stat(stats.mining)

public val Player.baseMiningLvl: Int
    get() = statBase(stats.mining)

public val Player.herbloreLvl: Int
    get() = stat(stats.herblore)

public val Player.baseHerbloreLvl: Int
    get() = statBase(stats.herblore)

public val Player.agilityLvl: Int
    get() = stat(stats.agility)

public val Player.baseAgilityLvl: Int
    get() = statBase(stats.agility)

public val Player.thievingLvl: Int
    get() = stat(stats.thieving)

public val Player.baseThievingLvl: Int
    get() = statBase(stats.thieving)

public val Player.slayerLvl: Int
    get() = stat(stats.slayer)

public val Player.baseSlayerLvl: Int
    get() = statBase(stats.slayer)

public val Player.farmingLvl: Int
    get() = stat(stats.farming)

public val Player.baseFarmingLvl: Int
    get() = statBase(stats.farming)

public val Player.runecraftingLvl: Int
    get() = stat(stats.runecrafting)

public val Player.baseRunecraftingLvl: Int
    get() = statBase(stats.runecrafting)

public val Player.hunterLvl: Int
    get() = stat(stats.hunter)

public val Player.baseHunterLvl: Int
    get() = statBase(stats.hunter)

public val Player.constructionLvl: Int
    get() = stat(stats.construction)

public val Player.baseConstructionLvl: Int
    get() = statBase(stats.construction)
