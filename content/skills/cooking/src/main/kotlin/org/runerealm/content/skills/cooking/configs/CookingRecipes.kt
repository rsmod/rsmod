package org.runerealm.content.skills.cooking.configs

import org.runerealm.game.type.obj.ObjType

/** Skilling success function parameters, as documented on the OSRS Wiki burn charts. */
data class CookRate(val low: Int, val high: Int)

/**
 * A raw food that can be cooked on a fire or range.
 *
 * Levels, experience and success rates are taken from the OSRS Wiki
 * (https://oldschool.runescape.wiki/w/Cooking and each food's burn chart).
 */
data class CookingRecipe(
    val raw: ObjType,
    val cooked: ObjType,
    val burnt: ObjType,
    val level: Int,
    val xp: Double,
    val fire: CookRate,
    val range: CookRate,
    /** Rates for the Lumbridge Castle range (Cook-o-matic 100) after Cook's Assistant. */
    val lumbridgeRange: CookRate,
    val successMessage: String,
    val burnMessage: String,
)

object CookingRecipes {
    private val tier1 = CookRate(low = 128, high = 512)
    private val tier1Lumbridge = CookRate(low = 138, high = 532)

    // TODO: Verify success/burn messages. The OSRS Wiki only documents the shrimp burn message.
    val shrimps =
        CookingRecipe(
            raw = cooking_objs.raw_shrimps,
            cooked = cooking_objs.shrimps,
            burnt = cooking_objs.burnt_shrimp,
            level = 1,
            xp = 30.0,
            fire = tier1,
            range = tier1,
            lumbridgeRange = tier1Lumbridge,
            successMessage = "You successfully cook some shrimps.",
            burnMessage = "You accidentally burn the shrimps.",
        )

    val anchovies =
        CookingRecipe(
            raw = cooking_objs.raw_anchovies,
            cooked = cooking_objs.anchovies,
            burnt = cooking_objs.burnt_fish_anchovies,
            level = 1,
            xp = 30.0,
            fire = tier1,
            range = tier1,
            lumbridgeRange = tier1Lumbridge,
            successMessage = "You successfully cook some anchovies.",
            burnMessage = "You accidentally burn the anchovies.",
        )

    val sardine =
        CookingRecipe(
            raw = cooking_objs.raw_sardine,
            cooked = cooking_objs.sardine,
            burnt = cooking_objs.burnt_fish_sardine,
            level = 1,
            xp = 40.0,
            fire = CookRate(low = 118, high = 492),
            range = CookRate(low = 118, high = 492),
            lumbridgeRange = CookRate(low = 128, high = 512),
            successMessage = "You successfully cook a sardine.",
            burnMessage = "You accidentally burn the sardine.",
        )

    val herring =
        CookingRecipe(
            raw = cooking_objs.raw_herring,
            cooked = cooking_objs.herring,
            burnt = cooking_objs.burnt_fish_herring,
            level = 5,
            xp = 50.0,
            fire = CookRate(low = 108, high = 472),
            range = CookRate(low = 108, high = 472),
            lumbridgeRange = CookRate(low = 118, high = 492),
            successMessage = "You successfully cook a herring.",
            burnMessage = "You accidentally burn the herring.",
        )

    val trout =
        CookingRecipe(
            raw = cooking_objs.raw_trout,
            cooked = cooking_objs.trout,
            burnt = cooking_objs.burnt_fish_trout,
            level = 15,
            xp = 70.0,
            fire = CookRate(low = 88, high = 432),
            range = CookRate(low = 88, high = 432),
            lumbridgeRange = CookRate(low = 98, high = 452),
            successMessage = "You successfully cook a trout.",
            burnMessage = "You accidentally burn the trout.",
        )

    val pike =
        CookingRecipe(
            raw = cooking_objs.raw_pike,
            cooked = cooking_objs.pike,
            burnt = cooking_objs.burnt_fish_trout,
            level = 20,
            xp = 80.0,
            fire = CookRate(low = 78, high = 412),
            range = CookRate(low = 78, high = 412),
            lumbridgeRange = CookRate(low = 88, high = 432),
            successMessage = "You successfully cook a pike.",
            burnMessage = "You accidentally burn the pike.",
        )

    val salmon =
        CookingRecipe(
            raw = cooking_objs.raw_salmon,
            cooked = cooking_objs.salmon,
            burnt = cooking_objs.burnt_fish_trout,
            level = 25,
            xp = 90.0,
            fire = CookRate(low = 68, high = 392),
            range = CookRate(low = 68, high = 392),
            lumbridgeRange = CookRate(low = 78, high = 402),
            successMessage = "You successfully cook a salmon.",
            burnMessage = "You accidentally burn the salmon.",
        )

    private fun meat(raw: ObjType) =
        CookingRecipe(
            raw = raw,
            cooked = cooking_objs.cooked_meat,
            burnt = cooking_objs.burnt_meat,
            level = 1,
            xp = 30.0,
            fire = tier1,
            range = tier1,
            lumbridgeRange = tier1Lumbridge,
            successMessage = "You cook a piece of meat.",
            burnMessage = "You accidentally burn the meat.",
        )

    // TODO: Raw beef/bear meat on a range can also be dried into sinew (members).
    val beef = meat(cooking_objs.raw_beef)
    val rat_meat = meat(cooking_objs.raw_rat_meat)
    val bear_meat = meat(cooking_objs.raw_bear_meat)

    val chicken =
        CookingRecipe(
            raw = cooking_objs.raw_chicken,
            cooked = cooking_objs.cooked_chicken,
            burnt = cooking_objs.burnt_chicken,
            level = 1,
            xp = 30.0,
            fire = tier1,
            range = tier1,
            lumbridgeRange = tier1Lumbridge,
            successMessage = "You cook the chicken.",
            burnMessage = "You accidentally burn the chicken.",
        )

    val all: List<CookingRecipe> =
        listOf(
            shrimps,
            anchovies,
            sardine,
            herring,
            trout,
            pike,
            salmon,
            beef,
            rat_meat,
            bear_meat,
            chicken,
        )
}
