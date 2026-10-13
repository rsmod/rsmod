package org.runerealm.content.skills.mining.configs

import org.runerealm.game.type.category.CategoryType
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.obj.ObjType

/** Skilling success function parameters, as documented on the OSRS Wiki success charts. */
data class MineRate(val low: Int, val high: Int)

/**
 * A possible product of a successful mining roll.
 *
 * Rocks with more than one product (granite and sandstone) roll each product in order, from the
 * heaviest to the lightest, until one roll succeeds ("cascade" on the wiki success charts).
 */
data class RockProduct(val obj: ObjType, val xp: Double, val rate: MineRate)

enum class RockKind {
    /** Standard ore rocks: deplete on every successful roll. */
    Ore,
    /** Gem rocks: the product is rolled from the gem rock table; a charged glory triples rates. */
    Gem,
    /** Amethyst crystals: can be mined a few times before depleting. */
    Amethyst,
    /** Rune essence rocks: never deplete and every roll succeeds. */
    Essence,
}

/**
 * A type of mining rock.
 *
 * Levels, experience, success rates and respawn times are taken from the OSRS Wiki pages of each
 * rock (e.g. [Copper rocks](https://oldschool.runescape.wiki/w/Copper_rocks)).
 *
 * @param categories the cache categories whose locs are this rock. Every mineable loc in these
 *   categories is registered, so all instances in the world work.
 * @param locs additional locs that do not have a dedicated cache category.
 * @param oreName the name used in messages, e.g. `"copper"` in `"This rock contains copper."`.
 * @param respawnTicks game ticks until a depleted rock respawns.
 */
data class MiningRock(
    val key: String,
    val level: Int,
    val products: List<RockProduct>,
    val respawnTicks: Int,
    val oreName: String,
    val successMessage: String,
    val categories: List<CategoryType> = emptyList(),
    val locs: List<LocType> = emptyList(),
    val kind: RockKind = RockKind.Ore,
) {
    val product: RockProduct
        get() = products.first()
}

object MiningRocks {
    private val objs = mining_objs
    private val cats = mining_categories
    private val locs = mining_locs

    private fun ore(
        key: String,
        level: Int,
        obj: ObjType,
        xp: Double,
        low: Int,
        high: Int,
        respawnTicks: Int,
        oreName: String,
        category: CategoryType,
        message: String = "You manage to mine some $oreName.",
    ): MiningRock =
        MiningRock(
            key = key,
            level = level,
            products = listOf(RockProduct(obj, xp, MineRate(low, high))),
            respawnTicks = respawnTicks,
            oreName = oreName,
            successMessage = message,
            categories = listOf(category),
        )

    val clay = ore("clay", 1, objs.clay, 5.0, 128, 400, 2, "clay", cats.clay_rocks)
    val copper = ore("copper", 1, objs.copper_ore, 17.5, 100, 350, 4, "copper", cats.copper_rocks)
    val tin = ore("tin", 1, objs.tin_ore, 17.5, 100, 350, 4, "tin", cats.tin_rocks)
    val blurite =
        ore("blurite", 10, objs.blurite_ore, 17.5, 90, 350, 42, "blurite", cats.blurite_rocks)
    val iron = ore("iron", 15, objs.iron_ore, 35.0, 96, 350, 9, "iron", cats.iron_rocks)
    val silver = ore("silver", 20, objs.silver_ore, 40.0, 25, 200, 100, "silver", cats.silver_rocks)
    val coal = ore("coal", 30, objs.coal, 50.0, 16, 100, 50, "coal", cats.coal_rocks)
    val gold = ore("gold", 40, objs.gold_ore, 65.0, 7, 75, 100, "gold", cats.gold_rocks)
    val mithril =
        ore("mithril", 55, objs.mithril_ore, 80.0, 4, 50, 200, "mithril", cats.mithril_rocks)
    val lovakite =
        ore("lovakite", 65, objs.lovakite_ore, 60.0, 4, 50, 58, "lovakite", cats.lovakite_rocks)
    val adamantite =
        ore(
            "adamantite",
            70,
            objs.adamantite_ore,
            95.0,
            2,
            25,
            400,
            "adamantite",
            cats.adamantite_rocks,
        )
    val soft_clay =
        ore("soft_clay", 70, objs.soft_clay, 5.0, 128, 400, 2, "soft clay", cats.soft_clay_rocks)
    val runite = ore("runite", 85, objs.runite_ore, 125.0, 1, 18, 1200, "runite", cats.runite_rocks)

    val gem =
        MiningRock(
            key = "gem",
            level = 40,
            // The obj is resolved from the gem rock table; it is only used for messages.
            products = listOf(RockProduct(objs.uncut_opal, 65.0, MineRate(28, 70))),
            respawnTicks = 99,
            oreName = "gems",
            successMessage = "You just mined",
            categories = listOf(cats.gem_rocks),
            kind = RockKind.Gem,
        )

    val amethyst =
        MiningRock(
            key = "amethyst",
            level = 92,
            products = listOf(RockProduct(objs.amethyst, 240.0, MineRate(-18, 10))),
            respawnTicks = 125,
            oreName = "amethyst",
            successMessage = "You manage to mine some amethyst.",
            categories = listOf(cats.amethyst_rocks),
            kind = RockKind.Amethyst,
        )

    val granite =
        MiningRock(
            key = "granite",
            level = 45,
            products =
                listOf(
                    RockProduct(objs.granite_5kg, 75.0, MineRate(6, 64)),
                    RockProduct(objs.granite_2kg, 60.0, MineRate(8, 75)),
                    RockProduct(objs.granite_500g, 50.0, MineRate(16, 100)),
                ),
            respawnTicks = 9,
            oreName = "granite",
            successMessage = "You manage to quarry some granite.",
            locs = listOf(locs.granite_rocks),
        )

    val sandstone =
        MiningRock(
            key = "sandstone",
            level = 35,
            products =
                listOf(
                    RockProduct(objs.sandstone_10kg, 60.0, MineRate(4, 50)),
                    RockProduct(objs.sandstone_5kg, 50.0, MineRate(8, 75)),
                    RockProduct(objs.sandstone_2kg, 40.0, MineRate(16, 100)),
                    RockProduct(objs.sandstone_1kg, 30.0, MineRate(25, 200)),
                ),
            respawnTicks = 9,
            oreName = "sandstone",
            successMessage = "You manage to quarry some sandstone.",
            locs = listOf(locs.sandstone_rocks),
        )

    val essence =
        MiningRock(
            key = "essence",
            level = 1,
            products = listOf(RockProduct(objs.rune_essence, 5.0, MineRate(256, 256))),
            respawnTicks = 0,
            oreName = "essence",
            successMessage = "You manage to mine some essence.",
            locs = listOf(locs.rune_essence, locs.lunar_rune_essence),
            kind = RockKind.Essence,
        )

    val all: List<MiningRock> =
        listOf(
            clay,
            copper,
            tin,
            blurite,
            iron,
            silver,
            coal,
            gold,
            gem,
            mithril,
            lovakite,
            adamantite,
            soft_clay,
            runite,
            amethyst,
            granite,
            sandstone,
            essence,
        )

    /** Players with at least this Mining level mine pure essence instead of rune essence. */
    const val PURE_ESSENCE_LEVEL: Int = 30

    /**
     * The gem rock table out of 128. ([Gem rocks](https://oldschool.runescape.wiki/w/Gem_rocks))
     */
    val gemRockTable: List<Pair<ObjType, Int>> =
        listOf(
            objs.uncut_opal to 60,
            objs.uncut_jade to 30,
            objs.uncut_red_topaz to 15,
            objs.uncut_sapphire to 9,
            objs.uncut_emerald to 5,
            objs.uncut_ruby to 5,
            objs.uncut_diamond to 4,
        )

    /**
     * The mining gem drop table, rolled before every ore roll, out of 128. The remaining weight
     * gives nothing. ([Mining](https://oldschool.runescape.wiki/w/Template:MiningPreGemDropTable))
     */
    val randomGemTable: List<Pair<ObjType, Int>> =
        listOf(
            objs.uncut_sapphire to 32,
            objs.uncut_emerald to 16,
            objs.uncut_ruby to 8,
            objs.uncut_diamond to 2,
        )

    const val GEM_TABLE_WEIGHT: Int = 128

    /** A `1/n` chance to roll the random gem table on each mining attempt. */
    const val RANDOM_GEM_CHANCE: Int = 256

    /** The `1/n` random gem chance while wearing a charged amulet of glory. */
    const val RANDOM_GEM_CHANCE_GLORY: Int = 86

    /** A `1/n` chance for an amethyst crystal to deplete after a successful roll. */
    const val AMETHYST_DEPLETE_CHANCE: Int = 2
}
