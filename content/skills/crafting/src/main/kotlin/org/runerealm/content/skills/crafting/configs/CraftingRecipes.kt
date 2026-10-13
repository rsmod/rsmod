package org.runerealm.content.skills.crafting.configs

import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.seq.SeqType

/**
 * Crafting products, per the OSRS Wiki ([Crafting](https://oldschool.runescape.wiki/w/Crafting)).
 */
object CraftingRecipes {
    private val objs = crafting_objs
    private val seqs = CraftingSeqs

    private const val SPIN_TICKS = 3
    private const val POTTERY_TICKS = 3
    private const val LEATHER_TICKS = 3
    private const val GEM_TICKS = 2
    private const val FURNACE_TICKS = 3
    private const val GLASS_TICKS = 3
    private const val STRING_TICKS = 2
    private const val STAFF_TICKS = 2

    /** Two inventory objs used together. */
    data class Combo(val first: ObjType, val second: ObjType, val options: List<CraftRecipe>)

    /* Spinning wheel */

    private fun spin(input: ObjType, product: ObjType, level: Int, xp: Double) =
        CraftRecipe(
            product = product,
            inputs = listOf(Ingredient(input)),
            level = level,
            xp = xp,
            anim = seqs.spinning,
            ticks = SPIN_TICKS,
        )

    val spinning: List<CraftRecipe> =
        listOf(
            spin(objs.wool, objs.ball_of_wool, 1, 2.5),
            spin(objs.flax, objs.bow_string, 10, 15.0),
            spin(objs.magic_roots, objs.magic_string, 19, 30.0),
            spin(objs.sinew, objs.crossbow_string, 10, 15.0),
        )

    /* Pottery */

    private fun wheel(product: ObjType, level: Int, xp: Double) =
        CraftRecipe(
            product = product,
            inputs = listOf(Ingredient(objs.soft_clay)),
            level = level,
            xp = xp,
            anim = seqs.pottery_wheel,
            ticks = POTTERY_TICKS,
        )

    val potteryWheel: List<CraftRecipe> =
        listOf(
            wheel(objs.pot_unfired, 1, 6.3),
            wheel(objs.pie_dish_unfired, 7, 15.0),
            wheel(objs.bowl_unfired, 8, 18.0),
            wheel(objs.plant_pot_unfired, 19, 20.0),
            wheel(objs.pot_lid_unfired, 25, 20.0),
        )

    private fun oven(unfired: ObjType, product: ObjType, level: Int, xp: Double) =
        CraftRecipe(
            product = product,
            inputs = listOf(Ingredient(unfired)),
            level = level,
            xp = xp,
            anim = seqs.furnace,
            ticks = POTTERY_TICKS,
        )

    val potteryOven: List<CraftRecipe> =
        listOf(
            oven(objs.pot_unfired, objs.pot, 1, 6.3),
            oven(objs.pie_dish_unfired, objs.pie_dish, 7, 10.0),
            oven(objs.bowl_unfired, objs.bowl_empty, 8, 15.0),
            oven(objs.plant_pot_unfired, objs.plant_pot, 19, 17.5),
            oven(objs.pot_lid_unfired, objs.pot_lid, 25, 20.0),
        )

    private fun softClay(water: ObjType, empty: ObjType) =
        Combo(
            water,
            objs.clay,
            listOf(
                CraftRecipe(
                    product = objs.soft_clay,
                    inputs = listOf(Ingredient(objs.clay), Ingredient(water)),
                    byproducts = listOf(Ingredient(empty)),
                    level = 1,
                    xp = 0.0,
                    ticks = 1,
                    message = "You mix the clay and water. You now have some soft, workable clay.",
                )
            ),
        )

    private val softClay =
        listOf(
            softClay(objs.bucket_water, objs.bucket_empty),
            softClay(objs.jug_water, objs.jug_empty),
            softClay(objs.bowl_water, objs.bowl_empty),
        )

    /* Leather */

    private fun sew(leather: ObjType, product: ObjType, count: Int, level: Int, xp: Double) =
        CraftRecipe(
            product = product,
            inputs = listOf(Ingredient(leather, count)),
            level = level,
            xp = xp,
            tools = listOf(objs.needle),
            usesThread = true,
            anim = seqs.leather,
            ticks = LEATHER_TICKS,
        )

    /** Dragonhide vambraces, chaps and body use one, two and three leathers respectively. */
    private fun dhide(
        leather: ObjType,
        vambs: ObjType,
        chaps: ObjType,
        body: ObjType,
        levels: Triple<Int, Int, Int>,
        xp: Double,
    ) =
        listOf(
            sew(leather, vambs, 1, levels.first, xp),
            sew(leather, chaps, 2, levels.second, xp * 2),
            sew(leather, body, 3, levels.third, xp * 3),
        )

    /** The options offered when using a needle on each type of leather, in interface order. */
    val needleOnLeather: Map<ObjType, List<CraftRecipe>> =
        linkedMapOf(
            objs.leather to
                listOf(
                    sew(objs.leather, objs.leather_gloves, 1, 1, 13.8),
                    sew(objs.leather, objs.leather_boots, 1, 7, 16.25),
                    sew(objs.leather, objs.leather_cowl, 1, 9, 18.5),
                    sew(objs.leather, objs.leather_vambraces, 1, 11, 22.0),
                    sew(objs.leather, objs.leather_body, 1, 14, 25.0),
                    sew(objs.leather, objs.leather_chaps, 1, 18, 27.0),
                    sew(objs.leather, objs.coif, 1, 38, 37.0),
                ),
            objs.hard_leather to listOf(sew(objs.hard_leather, objs.hardleather_body, 1, 28, 35.0)),
            objs.snakeskin to
                listOf(
                    sew(objs.snakeskin, objs.snakeskin_boots, 6, 45, 30.0),
                    sew(objs.snakeskin, objs.snakeskin_vambraces, 8, 47, 35.0),
                    sew(objs.snakeskin, objs.snakeskin_bandana, 5, 48, 45.0),
                    sew(objs.snakeskin, objs.snakeskin_chaps, 12, 51, 50.0),
                    sew(objs.snakeskin, objs.snakeskin_body, 15, 53, 55.0),
                ),
            objs.green_leather to
                dhide(
                    objs.green_leather,
                    objs.green_vambraces,
                    objs.green_chaps,
                    objs.green_body,
                    Triple(57, 60, 63),
                    62.0,
                ),
            objs.blue_leather to
                dhide(
                    objs.blue_leather,
                    objs.blue_vambraces,
                    objs.blue_chaps,
                    objs.blue_body,
                    Triple(66, 68, 71),
                    70.0,
                ),
            objs.red_leather to
                dhide(
                    objs.red_leather,
                    objs.red_vambraces,
                    objs.red_chaps,
                    objs.red_body,
                    Triple(73, 75, 77),
                    78.0,
                ),
            objs.black_leather to
                dhide(
                    objs.black_leather,
                    objs.black_vambraces,
                    objs.black_chaps,
                    objs.black_body,
                    Triple(79, 82, 84),
                    86.0,
                ),
        )

    private fun studded(armour: ObjType, product: ObjType, level: Int, xp: Double) =
        Combo(
            objs.steel_studs,
            armour,
            listOf(
                CraftRecipe(
                    product = product,
                    inputs = listOf(Ingredient(armour), Ingredient(objs.steel_studs)),
                    level = level,
                    xp = xp,
                    anim = seqs.leather,
                    ticks = LEATHER_TICKS,
                    message = "You add the studs to the armour.",
                )
            ),
        )

    private val studded =
        listOf(
            studded(objs.leather_body, objs.studded_body, 41, 40.0),
            studded(objs.leather_chaps, objs.studded_chaps, 44, 42.0),
        )

    /* Gem cutting */

    private fun cut(
        uncut: ObjType,
        cut: ObjType,
        level: Int,
        xp: Double,
        anim: SeqType,
        failure: CraftFailure? = null,
    ) =
        Combo(
            objs.chisel,
            uncut,
            listOf(
                CraftRecipe(
                    product = cut,
                    inputs = listOf(Ingredient(uncut)),
                    level = level,
                    xp = xp,
                    tools = listOf(objs.chisel),
                    failure = failure,
                    anim = anim,
                    ticks = GEM_TICKS,
                )
            ),
        )

    /** Semi-precious gems can be crushed; success rates from the OSRS Wiki gem cutting chart. */
    private fun crush(low: Int, high: Int, xp: Double) =
        CraftFailure(low, high, objs.crushed_gem, xp)

    private val gems =
        listOf(
            cut(objs.uncut_opal, objs.opal, 1, 15.0, seqs.cut_opal, crush(128, 250, 3.8)),
            cut(objs.uncut_jade, objs.jade, 13, 20.0, seqs.cut_jade, crush(100, 245, 5.0)),
            cut(
                objs.uncut_red_topaz,
                objs.red_topaz,
                16,
                25.0,
                seqs.cut_red_topaz,
                crush(90, 240, 6.3),
            ),
            cut(objs.uncut_sapphire, objs.sapphire, 20, 50.0, seqs.cut_sapphire),
            cut(objs.uncut_emerald, objs.emerald, 27, 67.5, seqs.cut_emerald),
            cut(objs.uncut_ruby, objs.ruby, 34, 85.0, seqs.cut_ruby),
            cut(objs.uncut_diamond, objs.diamond, 43, 107.5, seqs.cut_diamond),
            cut(objs.uncut_dragonstone, objs.dragonstone, 55, 137.5, seqs.cut_dragonstone),
            cut(objs.uncut_onyx, objs.onyx, 67, 167.5, seqs.cut_onyx),
            cut(objs.uncut_zenyte, objs.zenyte, 89, 50.0, seqs.cut_zenyte),
        )

    private fun amethyst(product: ObjType, count: Int, level: Int) =
        CraftRecipe(
            product = product,
            inputs = listOf(Ingredient(objs.amethyst)),
            level = level,
            xp = 60.0,
            productCount = count,
            tools = listOf(objs.chisel),
            anim = seqs.cut_amethyst,
            ticks = GEM_TICKS,
        )

    private val amethyst =
        Combo(
            objs.chisel,
            objs.amethyst,
            listOf(
                amethyst(objs.amethyst_bolt_tips, 15, 83),
                amethyst(objs.amethyst_arrowheads, 15, 85),
                amethyst(objs.amethyst_javelin_heads, 5, 87),
                amethyst(objs.amethyst_dart_tip, 8, 89),
            ),
        )

    /* Jewellery (furnace) */

    private fun jewel(
        bar: ObjType,
        gem: ObjType?,
        mould: ObjType,
        product: ObjType,
        level: Int,
        xp: Double,
        count: Int = 1,
    ) =
        CraftRecipe(
            product = product,
            inputs = listOfNotNull(Ingredient(bar), gem?.let(::Ingredient)),
            level = level,
            xp = xp,
            productCount = count,
            tools = listOf(mould),
            anim = seqs.furnace,
            ticks = FURNACE_TICKS,
        )

    private fun gold(gem: ObjType?, mould: ObjType, product: ObjType, level: Int, xp: Double) =
        jewel(objs.gold_bar, gem, mould, product, level, xp)

    private fun silver(
        gem: ObjType?,
        mould: ObjType,
        product: ObjType,
        level: Int,
        xp: Double,
        count: Int = 1,
    ) = jewel(objs.silver_bar, gem, mould, product, level, xp, count)

    /** Gold jewellery interface (`crafting_gold`) buttons and what they make. */
    val goldJewellery: Map<ComponentType, CraftRecipe> =
        CraftingGoldComponents.let { c ->
            val ring = objs.ring_mould
            val neck = objs.necklace_mould
            val ammy = objs.amulet_mould
            val brace = objs.bracelet_mould
            linkedMapOf(
                c.gold_ring to gold(null, ring, objs.gold_ring, 5, 15.0),
                c.sapphire_ring to gold(objs.sapphire, ring, objs.sapphire_ring, 20, 40.0),
                c.emerald_ring to gold(objs.emerald, ring, objs.emerald_ring, 27, 55.0),
                c.ruby_ring to gold(objs.ruby, ring, objs.ruby_ring, 34, 70.0),
                c.diamond_ring to gold(objs.diamond, ring, objs.diamond_ring, 43, 85.0),
                c.dragon_ring to gold(objs.dragonstone, ring, objs.dragonstone_ring, 55, 100.0),
                c.onyx_ring to gold(objs.onyx, ring, objs.onyx_ring, 67, 115.0),
                c.zenyte_ring to gold(objs.zenyte, ring, objs.zenyte_ring, 89, 150.0),
                c.gold_necklace to gold(null, neck, objs.gold_necklace, 6, 20.0),
                c.sapphire_necklace to gold(objs.sapphire, neck, objs.sapphire_necklace, 22, 55.0),
                c.emerald_necklace to gold(objs.emerald, neck, objs.emerald_necklace, 29, 60.0),
                c.ruby_necklace to gold(objs.ruby, neck, objs.ruby_necklace, 40, 75.0),
                c.diamond_necklace to gold(objs.diamond, neck, objs.diamond_necklace, 56, 90.0),
                c.dragon_necklace to gold(objs.dragonstone, neck, objs.dragon_necklace, 72, 105.0),
                c.onyx_necklace to gold(objs.onyx, neck, objs.onyx_necklace, 82, 120.0),
                c.zenyte_necklace to gold(objs.zenyte, neck, objs.zenyte_necklace, 92, 165.0),
                c.gold_amulet to gold(null, ammy, objs.gold_amulet_u, 8, 30.0),
                c.sapphire_amulet to gold(objs.sapphire, ammy, objs.sapphire_amulet_u, 24, 65.0),
                c.emerald_amulet to gold(objs.emerald, ammy, objs.emerald_amulet_u, 31, 70.0),
                c.ruby_amulet to gold(objs.ruby, ammy, objs.ruby_amulet_u, 50, 85.0),
                c.diamond_amulet to gold(objs.diamond, ammy, objs.diamond_amulet_u, 70, 100.0),
                c.dragon_amulet to
                    gold(objs.dragonstone, ammy, objs.dragonstone_amulet_u, 80, 150.0),
                c.onyx_amulet to gold(objs.onyx, ammy, objs.onyx_amulet_u, 90, 165.0),
                c.zenyte_amulet to gold(objs.zenyte, ammy, objs.zenyte_amulet_u, 98, 200.0),
                c.gold_bracelet to gold(null, brace, objs.gold_bracelet, 7, 25.0),
                c.sapphire_bracelet to gold(objs.sapphire, brace, objs.sapphire_bracelet, 23, 60.0),
                c.emerald_bracelet to gold(objs.emerald, brace, objs.emerald_bracelet, 30, 65.0),
                c.ruby_bracelet to gold(objs.ruby, brace, objs.ruby_bracelet, 42, 80.0),
                c.diamond_bracelet to gold(objs.diamond, brace, objs.diamond_bracelet, 58, 95.0),
                c.dragon_bracelet to
                    gold(objs.dragonstone, brace, objs.dragonstone_bracelet, 74, 110.0),
                c.onyx_bracelet to gold(objs.onyx, brace, objs.onyx_bracelet, 84, 125.0),
                c.zenyte_bracelet to gold(objs.zenyte, brace, objs.zenyte_bracelet, 95, 180.0),
            )
        }

    /** Silver crafting interface (`silver_crafting`) buttons and what they make. */
    val silverCrafting: Map<ComponentType, CraftRecipe> =
        SilverCraftingComponents.let { c ->
            val ring = objs.ring_mould
            val neck = objs.necklace_mould
            val ammy = objs.amulet_mould
            val brace = objs.bracelet_mould
            linkedMapOf(
                c.opal_ring to silver(objs.opal, ring, objs.opal_ring, 1, 10.0),
                c.jade_ring to silver(objs.jade, ring, objs.jade_ring, 13, 32.0),
                c.topaz_ring to silver(objs.red_topaz, ring, objs.topaz_ring, 16, 35.0),
                c.opal_necklace to silver(objs.opal, neck, objs.opal_necklace, 16, 35.0),
                c.jade_necklace to silver(objs.jade, neck, objs.jade_necklace, 25, 54.0),
                c.topaz_necklace to silver(objs.red_topaz, neck, objs.topaz_necklace, 32, 70.0),
                c.opal_amulet to silver(objs.opal, ammy, objs.opal_amulet_u, 27, 55.0),
                c.jade_amulet to silver(objs.jade, ammy, objs.jade_amulet_u, 34, 70.0),
                c.topaz_amulet to silver(objs.red_topaz, ammy, objs.topaz_amulet_u, 45, 80.0),
                c.opal_bracelet to silver(objs.opal, brace, objs.opal_bracelet, 22, 45.0),
                c.jade_bracelet to silver(objs.jade, brace, objs.jade_bracelet, 29, 60.0),
                c.topaz_bracelet to silver(objs.red_topaz, brace, objs.topaz_bracelet, 38, 75.0),
                c.holy_symbol to silver(null, objs.holy_mould, objs.unstrung_symbol, 16, 50.0),
                c.unholy_symbol to silver(null, objs.unholy_mould, objs.unstrung_emblem, 17, 50.0),
                c.sickle to silver(null, objs.sickle_mould, objs.silver_sickle, 18, 50.0),
                c.crossbow_bolt to
                    silver(null, objs.bolt_mould, objs.silver_bolts_unf, 21, 50.0, count = 10),
                c.tiara to silver(null, objs.tiara_mould, objs.tiara, 23, 52.5),
            )
        }

    /* Amulet stringing */

    private fun stringAmulet(unstrung: ObjType, strung: ObjType) =
        Combo(
            objs.ball_of_wool,
            unstrung,
            listOf(
                CraftRecipe(
                    product = strung,
                    inputs = listOf(Ingredient(unstrung), Ingredient(objs.ball_of_wool)),
                    level = 1,
                    xp = 4.0,
                    ticks = STRING_TICKS,
                    message = "You put some string on your amulet.",
                )
            ),
        )

    private val amuletStringing =
        listOf(
            stringAmulet(objs.gold_amulet_u, objs.gold_amulet),
            stringAmulet(objs.sapphire_amulet_u, objs.sapphire_amulet),
            stringAmulet(objs.emerald_amulet_u, objs.emerald_amulet),
            stringAmulet(objs.ruby_amulet_u, objs.ruby_amulet),
            stringAmulet(objs.diamond_amulet_u, objs.diamond_amulet),
            stringAmulet(objs.dragonstone_amulet_u, objs.dragonstone_amulet),
            stringAmulet(objs.onyx_amulet_u, objs.onyx_amulet),
            stringAmulet(objs.zenyte_amulet_u, objs.zenyte_amulet),
            stringAmulet(objs.opal_amulet_u, objs.opal_amulet),
            stringAmulet(objs.jade_amulet_u, objs.jade_amulet),
            stringAmulet(objs.topaz_amulet_u, objs.topaz_amulet),
        )

    /* Glass */

    /** Bucket of sand and soda ash, used on a furnace. */
    val moltenGlass: CraftRecipe =
        CraftRecipe(
            product = objs.molten_glass,
            inputs = listOf(Ingredient(objs.bucket_sand), Ingredient(objs.soda_ash)),
            byproducts = listOf(Ingredient(objs.bucket_empty)),
            level = 1,
            xp = 20.0,
            anim = seqs.furnace,
            ticks = FURNACE_TICKS,
            message = "You heat the sand and soda ash in the furnace to make glass.",
        )

    private fun blow(product: ObjType, level: Int, xp: Double) =
        CraftRecipe(
            product = product,
            inputs = listOf(Ingredient(objs.molten_glass)),
            level = level,
            xp = xp,
            tools = listOf(objs.glassblowing_pipe),
            anim = seqs.glassblowing,
            ticks = GLASS_TICKS,
        )

    private val glassblowing =
        Combo(
            objs.glassblowing_pipe,
            objs.molten_glass,
            listOf(
                blow(objs.beer_glass, 1, 17.5),
                blow(objs.candle_lantern, 4, 19.0),
                blow(objs.oil_lamp, 12, 25.0),
                blow(objs.vial, 33, 35.0),
                blow(objs.fishbowl, 42, 42.5),
                blow(objs.unpowered_orb, 46, 52.5),
                blow(objs.lantern_lens, 49, 55.0),
                blow(objs.light_orb, 87, 70.0),
            ),
        )

    /* Battlestaves */

    private fun battlestaff(orb: ObjType, staff: ObjType, level: Int, xp: Double) =
        Combo(
            orb,
            objs.battlestaff,
            listOf(
                CraftRecipe(
                    product = staff,
                    inputs = listOf(Ingredient(objs.battlestaff), Ingredient(orb)),
                    level = level,
                    xp = xp,
                    anim = seqs.battlestaff,
                    ticks = STAFF_TICKS,
                )
            ),
        )

    private val battlestaves =
        listOf(
            battlestaff(objs.water_orb, objs.water_battlestaff, 54, 100.0),
            battlestaff(objs.earth_orb, objs.earth_battlestaff, 58, 112.5),
            battlestaff(objs.fire_orb, objs.fire_battlestaff, 62, 125.0),
            battlestaff(objs.air_orb, objs.air_battlestaff, 66, 137.5),
        )

    val combos: List<Combo> =
        needleOnLeather.map { (leather, options) -> Combo(objs.needle, leather, options) } +
            softClay +
            studded +
            gems +
            amethyst +
            amuletStringing +
            glassblowing +
            battlestaves
}
