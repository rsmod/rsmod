package org.rsmod.content.items.food.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias fish_objs = FishObjs

internal object FishObjs : ObjReferences() {
    val shrimps = objs.shrimps
    val sardine = objs.sardine
    val herring = objs.herring
    val anchovies = objs.anchovies
    val mackerel = objs.mackerel
    val trout = objs.trout
    val bream = objs.bream
    val cod = objs.cod
    val pike = objs.pike
    val salmon = objs.salmon
    val tuna = objs.tuna
    val lobster = objs.lobster
    val bass = objs.bass
    val swordfish = objs.swordfish
    val monkfish = objs.monkfish
    val karambwan = objs.karambwan
    val shark = objs.shark
    val sea_turtle = objs.sea_turtle
    val manta_ray = objs.manta_ray
    val anglerfish = objs.anglerfish
    val dark_crab = objs.dark_crab
}

internal object FishObjEdits : ObjEditor() {
    init {
        edit(fish_objs.shrimps) {
            contentGroup = content.food
            param[params.food_heal_value] = 3
        }

        edit(fish_objs.sardine) {
            contentGroup = content.food
            param[params.food_heal_value] = 4
        }

        edit(fish_objs.herring) {
            contentGroup = content.food
            param[params.food_heal_value] = 5
        }

        edit(fish_objs.anchovies) {
            contentGroup = content.food
            param[params.food_heal_value] = 1
        }

        edit(fish_objs.mackerel) {
            contentGroup = content.food
            param[params.food_heal_value] = 6
        }

        edit(fish_objs.trout) {
            contentGroup = content.food
            param[params.food_heal_value] = 7
        }

        edit(fish_objs.bream) {
            contentGroup = content.food
        }

        edit(fish_objs.cod) {
            contentGroup = content.food
            param[params.food_heal_value] = 7
        }

        edit(fish_objs.pike) {
            contentGroup = content.food
            param[params.food_heal_value] = 8
        }

        edit(fish_objs.salmon) {
            contentGroup = content.food
            param[params.food_heal_value] = 9
        }

        edit(fish_objs.tuna) {
            contentGroup = content.food
            param[params.food_heal_value] = 10
        }

        edit(fish_objs.lobster) {
            contentGroup = content.food
            param[params.food_heal_value] = 12
        }

        edit(fish_objs.bass) {
            contentGroup = content.food
            param[params.food_heal_value] = 13
        }

        edit(fish_objs.swordfish) {
            contentGroup = content.food
            param[params.food_heal_value] = 14
        }

        edit(fish_objs.monkfish) {
            contentGroup = content.food
            param[params.food_heal_value] = 16
        }

        edit(fish_objs.karambwan) {
            contentGroup = content.food
            param[params.food_heal_value] = 18
            param[params.food_is_combo] = true
        }

        edit(fish_objs.shark) {
            contentGroup = content.food
            param[params.food_heal_value] = 20
        }

        edit(fish_objs.sea_turtle) {
            contentGroup = content.food
            param[params.food_heal_value] = 21
        }

        edit(fish_objs.manta_ray) {
            contentGroup = content.food
            param[params.food_heal_value] = 22
        }

        edit(fish_objs.anglerfish) {
            contentGroup = content.food
            param[params.food_overheal] = true
        }

        edit(fish_objs.dark_crab) {
            contentGroup = content.food
            param[params.food_heal_value] = 22
        }

    }
}
