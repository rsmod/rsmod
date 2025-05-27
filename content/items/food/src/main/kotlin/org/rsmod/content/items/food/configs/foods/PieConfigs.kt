package org.rsmod.content.items.food.configs.foods

import org.rsmod.api.config.refs.BaseStats.agility
import org.rsmod.api.config.refs.BaseStats.crafting
import org.rsmod.api.config.refs.BaseStats.farming
import org.rsmod.api.config.refs.BaseStats.fishing
import org.rsmod.api.config.refs.BaseStats.fletching
import org.rsmod.api.config.refs.BaseStats.herblore
import org.rsmod.api.config.refs.BaseStats.ranged
import org.rsmod.api.config.refs.BaseStats.slayer
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.items.food.configs.PIE_BOOST_3
import org.rsmod.content.items.food.configs.PIE_BOOST_4
import org.rsmod.content.items.food.configs.PIE_BOOST_5


internal typealias pie_objs = PieObjs

internal object PieObjs : ObjReferences() {

    val redberry_pie = find("redberry_pie")
    val redberry_pie_half = find("half_a_redberry_pie")
    val meat_pie = find("meat_pie")
    val meat_pie_half = find("half_a_meat_pie")
    val mud_pie = find("mud_pie")
    val apple_pie = find("apple_pie")
    val apple_pie_half = find("half_an_apple_pie")
    val garden_pie = find("garden_pie")
    val garden_pie_half = find("half_garden_pie")
    val fish_pie = find("fish_pie")
    val fish_pie_half = find("half_fish_pie")
    val botanical_pie = find("botanical_pie")
    val botanical_pie_half = find("half_botanical_pie")
    val mushroom_pie = find("mushroom_pie")
    val mushroom_pie_half = find("half_mushroom_pie")
    val admiral_pie = find("admiral_pie")
    val admiral_pie_half = find("half_admiral_pie")
    val dragonfruit_pie = find("dragonfruit_pie")
    val dragonfruit_pie_half = find("half_dragonfruit_pie")
    val wild_pie = find("wild_pie")
    val wild_pie_half = find("half_wild_pie")
    val summer_pie = find("summer_pie")
    val summer_pie_half = find("half_summer_pie")
}

internal object PieObjEdits : ObjEditor() {
    init {
        edit(type = pie_objs.redberry_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.redberry_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 5
            param[params.food_is_combo] = true
        }

        edit(type = pie_objs.redberry_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 5
            param[params.food_is_combo] = true
        }

        edit(type = pie_objs.meat_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.meat_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
        }

        edit(type = pie_objs.meat_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
        }

        edit(type = pie_objs.garden_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.garden_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = farming
            param[params.boosted_skill1_value] = PIE_BOOST_3
        }

        edit(type = pie_objs.garden_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = farming
            param[params.boosted_skill1_value] = PIE_BOOST_3
        }

        edit(type = pie_objs.fish_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.fish_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = fishing
            param[params.boosted_skill1_value] = PIE_BOOST_3
        }

        edit(type = pie_objs.fish_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = fishing
            param[params.boosted_skill1_value] = PIE_BOOST_3
        }

        edit(type = pie_objs.apple_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.apple_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 7
            param[params.food_is_combo] = true
        }

        edit(type = pie_objs.apple_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 7
            param[params.food_is_combo] = true
        }

        edit(type = pie_objs.botanical_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.botanical_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 7
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = herblore
            param[params.boosted_skill1_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.botanical_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = herblore
            param[params.boosted_skill1_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.mushroom_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.mushroom_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 8
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = crafting
            param[params.boosted_skill1_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.mushroom_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 8
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = crafting
            param[params.boosted_skill1_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.admiral_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.admiral_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 8
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = fishing
            param[params.boosted_skill1_value] = PIE_BOOST_5
        }

        edit(type = pie_objs.admiral_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 8
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = fishing
            param[params.boosted_skill1_value] = PIE_BOOST_5
        }

        edit(type = pie_objs.dragonfruit_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.dragonfruit_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 10
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = fletching
            param[params.boosted_skill1_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.dragonfruit_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 10
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = fletching
            param[params.boosted_skill1_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.wild_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.wild_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 11
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = slayer
            param[params.boosted_skill1_value] = PIE_BOOST_5
            param[params.boosted_skill2] = ranged
            param[params.boosted_skill2_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.wild_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 11
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = slayer
            param[params.boosted_skill1_value] = PIE_BOOST_5
            param[params.boosted_skill2] = ranged
            param[params.boosted_skill2_value] = PIE_BOOST_4
        }

        edit(type = pie_objs.summer_pie) {
            contentGroup = content.food
            param[params.food_replacement] = pie_objs.summer_pie_half
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = agility
            param[params.boosted_skill1_value] = PIE_BOOST_5
        }

        edit(type = pie_objs.summer_pie_half) {
            contentGroup = content.food
            param[params.food_replacement] = objs.pie_dish
            param[params.food_requires_replacement] = true
            param[params.food_heal_value] = 6
            param[params.food_is_combo] = true
            param[params.boosted_skill1] = agility
            param[params.boosted_skill1_value] = PIE_BOOST_5
        }
    }
}
