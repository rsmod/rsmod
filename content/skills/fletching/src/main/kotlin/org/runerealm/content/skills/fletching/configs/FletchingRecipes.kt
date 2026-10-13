package org.runerealm.content.skills.fletching.configs

import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.seq.SeqType

/**
 * Fletching products, per the OSRS Wiki
 * ([Fletching](https://oldschool.runescape.wiki/w/Fletching)).
 */
object FletchingRecipes {
    private val objs = fletching_objs
    private val seqs = FletchingSeqs

    private const val KNIFE_TICKS = 3
    private const val STRING_TICKS = 2
    private const val AMMO_TICKS = 2
    private const val ASSEMBLE_TICKS = 3
    private const val GEM_TICKS = 2

    private const val ARROW_BATCH = 15
    private const val JAVELIN_BATCH = 15
    private const val BOLT_BATCH = 10
    private const val DART_BATCH = 10

    /* Knife on logs */

    private fun shafts(log: ObjType, level: Int, count: Int, xp: Double) =
        FletchRecipe(
            product = objs.arrow_shaft,
            inputs = listOf(Ingredient(log)),
            level = level,
            xp = xp,
            productCount = count,
            tools = listOf(objs.knife),
            anim = seqs.knife,
            ticks = KNIFE_TICKS,
            message = "You carefully cut the wood into $count arrow shafts.",
        )

    private fun whittle(log: ObjType, product: ObjType, level: Int, xp: Double, what: String) =
        FletchRecipe(
            product = product,
            inputs = listOf(Ingredient(log)),
            level = level,
            xp = xp,
            tools = listOf(objs.knife),
            anim = seqs.knife,
            ticks = KNIFE_TICKS,
            message = "You carefully cut the logs into $what.",
        )

    private fun stock(log: ObjType, product: ObjType, level: Int, xp: Double) =
        whittle(log, product, level, xp, "a crossbow stock")

    private fun shortbow(log: ObjType, product: ObjType, level: Int, xp: Double) =
        whittle(log, product, level, xp, "a shortbow")

    private fun longbow(log: ObjType, product: ObjType, level: Int, xp: Double) =
        whittle(log, product, level, xp, "a longbow")

    /** The options offered when using a knife on each type of log, in interface order. */
    val knifeOnLogs: Map<ObjType, List<FletchRecipe>> =
        linkedMapOf(
            objs.logs to
                listOf(
                    shafts(objs.logs, level = 1, count = 15, xp = 5.0),
                    FletchRecipe(
                        product = objs.javelin_shaft,
                        inputs = listOf(Ingredient(objs.logs)),
                        level = 3,
                        xp = 5.0,
                        productCount = 15,
                        tools = listOf(objs.knife),
                        anim = seqs.knife,
                        ticks = KNIFE_TICKS,
                        message = "You carefully cut the wood into 15 javelin shafts.",
                    ),
                    shortbow(objs.logs, objs.shortbow_u, 5, 5.0),
                    longbow(objs.logs, objs.longbow_u, 10, 10.0),
                    stock(objs.logs, objs.wooden_stock, 9, 6.0),
                ),
            objs.oak_logs to
                listOf(
                    shafts(objs.oak_logs, level = 15, count = 30, xp = 10.0),
                    shortbow(objs.oak_logs, objs.oak_shortbow_u, 20, 16.5),
                    longbow(objs.oak_logs, objs.oak_longbow_u, 25, 25.0),
                    stock(objs.oak_logs, objs.oak_stock, 24, 16.0),
                ),
            objs.willow_logs to
                listOf(
                    shafts(objs.willow_logs, level = 30, count = 45, xp = 15.0),
                    shortbow(objs.willow_logs, objs.willow_shortbow_u, 35, 33.3),
                    longbow(objs.willow_logs, objs.willow_longbow_u, 40, 41.5),
                    stock(objs.willow_logs, objs.willow_stock, 39, 22.0),
                ),
            objs.teak_logs to listOf(stock(objs.teak_logs, objs.teak_stock, 46, 27.0)),
            objs.maple_logs to
                listOf(
                    shafts(objs.maple_logs, level = 45, count = 60, xp = 20.0),
                    shortbow(objs.maple_logs, objs.maple_shortbow_u, 50, 50.0),
                    longbow(objs.maple_logs, objs.maple_longbow_u, 55, 58.3),
                    stock(objs.maple_logs, objs.maple_stock, 54, 32.0),
                ),
            objs.mahogany_logs to listOf(stock(objs.mahogany_logs, objs.mahogany_stock, 61, 41.0)),
            objs.yew_logs to
                listOf(
                    shafts(objs.yew_logs, level = 60, count = 75, xp = 25.0),
                    shortbow(objs.yew_logs, objs.yew_shortbow_u, 65, 67.5),
                    longbow(objs.yew_logs, objs.yew_longbow_u, 70, 75.0),
                    stock(objs.yew_logs, objs.yew_stock, 69, 50.0),
                ),
            objs.magic_logs to
                listOf(
                    shafts(objs.magic_logs, level = 75, count = 90, xp = 30.0),
                    shortbow(objs.magic_logs, objs.magic_shortbow_u, 80, 83.3),
                    longbow(objs.magic_logs, objs.magic_longbow_u, 85, 91.5),
                    stock(objs.magic_logs, objs.magic_stock, 78, 70.0),
                ),
            objs.redwood_logs to
                listOf(shafts(objs.redwood_logs, level = 90, count = 105, xp = 35.0)),
        )

    /* Two-item combinations (the first obj is used on the second, or vice versa). */

    data class Combo(val first: ObjType, val second: ObjType, val recipe: FletchRecipe)

    private fun bow(unstrung: ObjType, strung: ObjType, level: Int, xp: Double, anim: SeqType) =
        Combo(
            objs.bow_string,
            unstrung,
            FletchRecipe(
                product = strung,
                inputs = listOf(Ingredient(unstrung), Ingredient(objs.bow_string)),
                level = level,
                xp = xp,
                anim = anim,
                ticks = STRING_TICKS,
                message = "You add a string to the bow.",
            ),
        )

    private val bows =
        listOf(
            bow(objs.shortbow_u, objs.shortbow, 5, 5.0, seqs.string_shortbow),
            bow(objs.longbow_u, objs.longbow, 10, 10.0, seqs.string_longbow),
            bow(objs.oak_shortbow_u, objs.oak_shortbow, 20, 16.5, seqs.string_oak_shortbow),
            bow(objs.oak_longbow_u, objs.oak_longbow, 25, 25.0, seqs.string_oak_longbow),
            bow(
                objs.willow_shortbow_u,
                objs.willow_shortbow,
                35,
                33.2,
                seqs.string_willow_shortbow,
            ),
            bow(objs.willow_longbow_u, objs.willow_longbow, 40, 41.5, seqs.string_willow_longbow),
            bow(objs.maple_shortbow_u, objs.maple_shortbow, 50, 50.0, seqs.string_maple_shortbow),
            bow(objs.maple_longbow_u, objs.maple_longbow, 55, 58.2, seqs.string_maple_longbow),
            bow(objs.yew_shortbow_u, objs.yew_shortbow, 65, 67.5, seqs.string_yew_shortbow),
            bow(objs.yew_longbow_u, objs.yew_longbow, 70, 75.0, seqs.string_yew_longbow),
            bow(objs.magic_shortbow_u, objs.magic_shortbow, 80, 83.2, seqs.string_magic_shortbow),
            bow(objs.magic_longbow_u, objs.magic_longbow, 85, 91.5, seqs.string_magic_longbow),
        )

    private fun crossbow(
        stock: ObjType,
        limbs: ObjType,
        unstrung: ObjType,
        strung: ObjType,
        level: Int,
        assembleXp: Double,
        stringXp: Double,
        assembleAnim: SeqType,
        stringAnim: SeqType,
    ): List<Combo> =
        listOf(
            Combo(
                limbs,
                stock,
                FletchRecipe(
                    product = unstrung,
                    inputs = listOf(Ingredient(stock), Ingredient(limbs)),
                    level = level,
                    xp = assembleXp,
                    tools = listOf(objs.hammer),
                    anim = assembleAnim,
                    ticks = ASSEMBLE_TICKS,
                    message = "You attach the metal limbs to the stock.",
                ),
            ),
            Combo(
                objs.crossbow_string,
                unstrung,
                FletchRecipe(
                    product = strung,
                    inputs = listOf(Ingredient(unstrung), Ingredient(objs.crossbow_string)),
                    level = level,
                    xp = stringXp,
                    anim = stringAnim,
                    ticks = STRING_TICKS,
                    message = "You add a string to the crossbow.",
                ),
            ),
        )

    private val crossbows =
        crossbow(
            objs.wooden_stock,
            objs.bronze_limbs,
            objs.bronze_crossbow_u,
            objs.bronze_crossbow,
            9,
            12.0,
            6.0,
            seqs.assemble_bronze_crossbow,
            seqs.string_bronze_crossbow,
        ) +
            crossbow(
                objs.oak_stock,
                objs.blurite_limbs,
                objs.blurite_crossbow_u,
                objs.blurite_crossbow,
                24,
                32.0,
                16.0,
                seqs.assemble_blurite_crossbow,
                seqs.string_blurite_crossbow,
            ) +
            crossbow(
                objs.willow_stock,
                objs.iron_limbs,
                objs.iron_crossbow_u,
                objs.iron_crossbow,
                39,
                44.0,
                22.0,
                seqs.assemble_iron_crossbow,
                seqs.string_iron_crossbow,
            ) +
            crossbow(
                objs.teak_stock,
                objs.steel_limbs,
                objs.steel_crossbow_u,
                objs.steel_crossbow,
                46,
                54.0,
                27.0,
                seqs.assemble_steel_crossbow,
                seqs.string_steel_crossbow,
            ) +
            crossbow(
                objs.maple_stock,
                objs.mithril_limbs,
                objs.mithril_crossbow_u,
                objs.mithril_crossbow,
                54,
                64.0,
                32.0,
                seqs.assemble_mithril_crossbow,
                seqs.string_mithril_crossbow,
            ) +
            crossbow(
                objs.mahogany_stock,
                objs.adamant_limbs,
                objs.adamant_crossbow_u,
                objs.adamant_crossbow,
                61,
                82.0,
                41.0,
                seqs.assemble_adamant_crossbow,
                seqs.string_adamant_crossbow,
            ) +
            crossbow(
                objs.yew_stock,
                objs.runite_limbs,
                objs.runite_crossbow_u,
                objs.runite_crossbow,
                69,
                100.0,
                50.0,
                seqs.assemble_runite_crossbow,
                seqs.string_runite_crossbow,
            ) +
            crossbow(
                objs.magic_stock,
                objs.dragon_limbs,
                objs.dragon_crossbow_u,
                objs.dragon_crossbow,
                78,
                135.0,
                70.0,
                seqs.assemble_dragon_crossbow,
                seqs.string_dragon_crossbow,
            )

    private val headlessArrows =
        Combo(
            objs.feather,
            objs.arrow_shaft,
            FletchRecipe(
                product = objs.headless_arrow,
                inputs = listOf(Ingredient(objs.arrow_shaft), Ingredient(objs.feather)),
                level = 1,
                xp = 1.0,
                batch = ARROW_BATCH,
                anim = seqs.add_feather,
                ticks = AMMO_TICKS,
                message = "You attach feathers to {count} arrow shafts.",
            ),
        )

    private fun arrow(tips: ObjType, arrow: ObjType, level: Int, xp: Double) =
        Combo(
            tips,
            objs.headless_arrow,
            FletchRecipe(
                product = arrow,
                inputs = listOf(Ingredient(objs.headless_arrow), Ingredient(tips)),
                level = level,
                xp = xp,
                batch = ARROW_BATCH,
                anim = seqs.add_arrow_tips,
                ticks = AMMO_TICKS,
                message = "You attach arrowheads to {count} arrow shafts.",
            ),
        )

    private val arrows =
        listOf(
            arrow(objs.bronze_arrowheads, objs.bronze_arrow, 1, 1.3),
            arrow(objs.iron_arrowheads, objs.iron_arrow, 15, 2.5),
            arrow(objs.steel_arrowheads, objs.steel_arrow, 30, 5.0),
            arrow(objs.mithril_arrowheads, objs.mithril_arrow, 45, 7.5),
            arrow(objs.adamant_arrowheads, objs.adamant_arrow, 60, 10.0),
            arrow(objs.rune_arrowheads, objs.rune_arrow, 75, 12.5),
            arrow(objs.amethyst_arrowheads, objs.amethyst_arrow, 82, 13.5),
            arrow(objs.dragon_arrowheads, objs.dragon_arrow, 90, 15.0),
        )

    private fun bolt(unf: ObjType, bolt: ObjType, level: Int, xp: Double, anim: SeqType) =
        Combo(
            objs.feather,
            unf,
            FletchRecipe(
                product = bolt,
                inputs = listOf(Ingredient(unf), Ingredient(objs.feather)),
                level = level,
                xp = xp,
                batch = BOLT_BATCH,
                anim = anim,
                ticks = AMMO_TICKS,
                message = "You fletch {count} bolts.",
            ),
        )

    private val bolts =
        listOf(
            bolt(objs.bronze_bolts_unf, objs.bronze_bolts, 9, 0.5, seqs.bolt_feathers_bronze),
            bolt(objs.blurite_bolts_unf, objs.blurite_bolts, 24, 1.0, seqs.bolt_feathers_blurite),
            bolt(objs.iron_bolts_unf, objs.iron_bolts, 39, 1.5, seqs.bolt_feathers_iron),
            bolt(objs.silver_bolts_unf, objs.silver_bolts, 43, 2.5, seqs.bolt_feathers_silver),
            bolt(objs.steel_bolts_unf, objs.steel_bolts, 46, 3.5, seqs.bolt_feathers_steel),
            bolt(objs.mithril_bolts_unf, objs.mithril_bolts, 54, 5.0, seqs.bolt_feathers_mithril),
            bolt(objs.adamant_bolts_unf, objs.adamant_bolts, 61, 7.0, seqs.bolt_feathers_adamant),
            bolt(objs.runite_bolts_unf, objs.runite_bolts, 69, 10.0, seqs.bolt_feathers_rune),
            bolt(objs.dragon_bolts_unf, objs.dragon_bolts, 84, 12.0, seqs.bolt_feathers_dragon),
        )

    private fun boltTips(
        gem: ObjType,
        tips: ObjType,
        count: Int,
        level: Int,
        xp: Double,
        anim: SeqType,
    ) =
        Combo(
            objs.chisel,
            gem,
            FletchRecipe(
                product = tips,
                inputs = listOf(Ingredient(gem)),
                level = level,
                xp = xp,
                productCount = count,
                tools = listOf(objs.chisel),
                anim = anim,
                ticks = GEM_TICKS,
                message = "You use your chisel to fletch $count bolt tips.",
            ),
        )

    private val boltTips =
        listOf(
            boltTips(objs.opal, objs.opal_bolt_tips, 12, 11, 1.5, seqs.cut_opal),
            boltTips(objs.jade, objs.jade_bolt_tips, 12, 26, 2.0, seqs.cut_jade),
            boltTips(objs.oyster_pearl, objs.pearl_bolt_tips, 6, 41, 3.2, seqs.cut_generic),
            boltTips(objs.oyster_pearls, objs.pearl_bolt_tips, 24, 41, 3.2, seqs.cut_generic),
            boltTips(objs.red_topaz, objs.topaz_bolt_tips, 12, 48, 3.9, seqs.cut_red_topaz),
            boltTips(objs.sapphire, objs.sapphire_bolt_tips, 12, 56, 4.0, seqs.cut_sapphire),
            boltTips(objs.emerald, objs.emerald_bolt_tips, 12, 58, 5.5, seqs.cut_emerald),
            boltTips(objs.ruby, objs.ruby_bolt_tips, 12, 63, 6.3, seqs.cut_ruby),
            boltTips(objs.diamond, objs.diamond_bolt_tips, 12, 65, 7.0, seqs.cut_diamond),
            boltTips(
                objs.dragonstone,
                objs.dragonstone_bolt_tips,
                12,
                71,
                8.2,
                seqs.cut_dragonstone,
            ),
            boltTips(objs.onyx, objs.onyx_bolt_tips, 24, 73, 9.4, seqs.cut_onyx),
        )

    private fun tipped(
        tips: ObjType,
        bolts: ObjType,
        product: ObjType,
        level: Int,
        xp: Double,
        anim: SeqType,
    ) =
        Combo(
            tips,
            bolts,
            FletchRecipe(
                product = product,
                inputs = listOf(Ingredient(bolts), Ingredient(tips)),
                level = level,
                xp = xp,
                batch = BOLT_BATCH,
                anim = anim,
                ticks = AMMO_TICKS,
                message = "You fletch {count} bolts.",
            ),
        )

    private val tippedBolts =
        listOf(
            tipped(
                objs.opal_bolt_tips,
                objs.bronze_bolts,
                objs.opal_bolts,
                11,
                1.6,
                seqs.bolt_tips_bronze,
            ),
            tipped(
                objs.jade_bolt_tips,
                objs.blurite_bolts,
                objs.jade_bolts,
                26,
                2.4,
                seqs.bolt_tips_blurite,
            ),
            tipped(
                objs.pearl_bolt_tips,
                objs.iron_bolts,
                objs.pearl_bolts,
                41,
                3.2,
                seqs.bolt_tips_iron,
            ),
            tipped(
                objs.topaz_bolt_tips,
                objs.steel_bolts,
                objs.topaz_bolts,
                48,
                3.9,
                seqs.bolt_tips_steel,
            ),
            tipped(
                objs.barbed_bolt_tips,
                objs.bronze_bolts,
                objs.barbed_bolts,
                51,
                9.5,
                seqs.bolt_tips_bronze,
            ),
            tipped(
                objs.sapphire_bolt_tips,
                objs.mithril_bolts,
                objs.sapphire_bolts,
                56,
                4.7,
                seqs.bolt_tips_mithril,
            ),
            tipped(
                objs.emerald_bolt_tips,
                objs.mithril_bolts,
                objs.emerald_bolts,
                58,
                5.5,
                seqs.bolt_tips_mithril,
            ),
            tipped(
                objs.ruby_bolt_tips,
                objs.adamant_bolts,
                objs.ruby_bolts,
                63,
                6.3,
                seqs.bolt_tips_adamant,
            ),
            tipped(
                objs.diamond_bolt_tips,
                objs.adamant_bolts,
                objs.diamond_bolts,
                65,
                7.0,
                seqs.bolt_tips_adamant,
            ),
            tipped(
                objs.dragonstone_bolt_tips,
                objs.runite_bolts,
                objs.dragonstone_bolts,
                71,
                8.2,
                seqs.bolt_tips_rune,
            ),
            tipped(
                objs.onyx_bolt_tips,
                objs.runite_bolts,
                objs.onyx_bolts,
                73,
                9.4,
                seqs.bolt_tips_rune,
            ),
        )

    private fun dragonTipped(tips: ObjType, product: ObjType, xp: Double) =
        tipped(tips, objs.dragon_bolts, product, 84, xp, seqs.bolt_tips_dragon)

    private val dragonTippedBolts =
        listOf(
            dragonTipped(objs.opal_bolt_tips, objs.opal_dragon_bolts, 1.6),
            dragonTipped(objs.jade_bolt_tips, objs.jade_dragon_bolts, 2.4),
            dragonTipped(objs.pearl_bolt_tips, objs.pearl_dragon_bolts, 3.2),
            dragonTipped(objs.topaz_bolt_tips, objs.topaz_dragon_bolts, 4.0),
            dragonTipped(objs.sapphire_bolt_tips, objs.sapphire_dragon_bolts, 4.7),
            dragonTipped(objs.emerald_bolt_tips, objs.emerald_dragon_bolts, 5.5),
            dragonTipped(objs.ruby_bolt_tips, objs.ruby_dragon_bolts, 6.3),
            dragonTipped(objs.diamond_bolt_tips, objs.diamond_dragon_bolts, 7.0),
            dragonTipped(objs.dragonstone_bolt_tips, objs.dragonstone_dragon_bolts, 8.2),
            dragonTipped(objs.onyx_bolt_tips, objs.onyx_dragon_bolts, 9.4),
        )

    private fun dart(tip: ObjType, dart: ObjType, level: Int, xp: Double, anim: SeqType) =
        Combo(
            objs.feather,
            tip,
            FletchRecipe(
                product = dart,
                inputs = listOf(Ingredient(tip), Ingredient(objs.feather)),
                level = level,
                xp = xp,
                batch = DART_BATCH,
                anim = anim,
                ticks = AMMO_TICKS,
                message = "You finish making {count} darts.",
            ),
        )

    private val darts =
        listOf(
            dart(objs.bronze_dart_tip, objs.bronze_dart, 10, 1.8, seqs.dart_feathers_bronze),
            dart(objs.iron_dart_tip, objs.iron_dart, 22, 3.8, seqs.dart_feathers_iron),
            dart(objs.steel_dart_tip, objs.steel_dart, 37, 7.5, seqs.dart_feathers_steel),
            dart(objs.mithril_dart_tip, objs.mithril_dart, 52, 11.2, seqs.dart_feathers_mithril),
            dart(objs.adamant_dart_tip, objs.adamant_dart, 67, 15.0, seqs.dart_feathers_adamant),
            dart(objs.rune_dart_tip, objs.rune_dart, 81, 18.8, seqs.dart_feathers_rune),
            dart(objs.amethyst_dart_tip, objs.amethyst_dart, 90, 21.0, seqs.dart_feathers_amethyst),
            dart(objs.dragon_dart_tip, objs.dragon_dart, 95, 25.0, seqs.dart_feathers_dragon),
        )

    private fun javelin(heads: ObjType, javelin: ObjType, level: Int, xp: Double) =
        Combo(
            heads,
            objs.javelin_shaft,
            FletchRecipe(
                product = javelin,
                inputs = listOf(Ingredient(objs.javelin_shaft), Ingredient(heads)),
                level = level,
                xp = xp,
                batch = JAVELIN_BATCH,
                ticks = AMMO_TICKS,
                message = "You attach javelin heads to {count} javelin shafts.",
            ),
        )

    private val javelins =
        listOf(
            javelin(objs.bronze_javelin_heads, objs.bronze_javelin, 3, 1.0),
            javelin(objs.iron_javelin_heads, objs.iron_javelin, 17, 2.0),
            javelin(objs.steel_javelin_heads, objs.steel_javelin, 32, 5.0),
            javelin(objs.mithril_javelin_heads, objs.mithril_javelin, 47, 8.0),
            javelin(objs.adamant_javelin_heads, objs.adamant_javelin, 62, 10.0),
            javelin(objs.rune_javelin_heads, objs.rune_javelin, 77, 12.4),
            javelin(objs.amethyst_javelin_heads, objs.amethyst_javelin, 84, 13.5),
            javelin(objs.dragon_javelin_heads, objs.dragon_javelin, 92, 15.0),
        )

    val combos: List<Combo> =
        bows +
            crossbows +
            headlessArrows +
            arrows +
            bolts +
            boltTips +
            tippedBolts +
            dragonTippedBolts +
            darts +
            javelins
}
