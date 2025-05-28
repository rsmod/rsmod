package org.rsmod.content.items.food.configs.foods

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias meat_objs = MeatObjs

internal object MeatObjs : ObjReferences() {
    val cooked_kebbit = find("wildkebbit_cooked")
    val cooked_larupia = find("larupia_cooked")
    val cooked_barb_kebbit = find("barbkebbit_cooked")
    val cooked_graahk = find("graahk_cooked")
    val cooked_kyatt = find("kyatt_cooked")
    val cooked_pyre_fox = find("fennecfox_cooked")
    val cooked_sunlight_antelope = find("antelopesun_cooked")
    val cooked_dashing_kebbit = find("dashingkebbit_cooked")
    val cooked_moonlight_antelope = find("antelopemoon_cooked")
    val cooked_chicken = find("cooked_chicken")
    val cooked_meat = find("cooked_meat")
}

internal object MeatObjEdits : ObjEditor() {
    init {
        edit (meat_objs.cooked_chicken) {
            contentGroup = content.food
            param[params.food_heal_value] = 3
        }

        edit (meat_objs.cooked_meat) {
            contentGroup = content.food
            param[params.food_heal_value] = 3
        }
        edit(meat_objs.cooked_kebbit) {
            contentGroup = content.food
            param[params.food_heal_value] = 4
            param[params.food_secondary_heal] = 4
        }

        edit(meat_objs.cooked_larupia) {
            contentGroup = content.food
            param[params.food_heal_value] = 6
            param[params.food_secondary_heal] = 5
        }

        edit(meat_objs.cooked_graahk) {
            contentGroup = content.food
            param[params.food_heal_value] = 8
            param[params.food_secondary_heal] = 6
        }

        edit(meat_objs.cooked_barb_kebbit) {
            contentGroup = content.food
            param[params.food_heal_value] = 7
            param[params.food_secondary_heal] = 5
        }

        edit(meat_objs.cooked_sunlight_antelope) {
            contentGroup = content.food
            param[params.food_heal_value] = 8
            param[params.food_secondary_heal] = 6
        }

        edit(meat_objs.cooked_kyatt) {
            contentGroup = content.food
            param[params.food_heal_value] = 9
            param[params.food_secondary_heal] = 8
        }

        edit(meat_objs.cooked_pyre_fox) {
            contentGroup = content.food
            param[params.food_heal_value] = 11
            param[params.food_secondary_heal] = 8
        }

        edit(meat_objs.cooked_sunlight_antelope) {
            contentGroup = content.food
            param[params.food_heal_value] = 12
            param[params.food_secondary_heal] = 9
        }

        edit(meat_objs.cooked_dashing_kebbit) {
            contentGroup = content.food
            param[params.food_heal_value] = 13
            param[params.food_secondary_heal] = 10
        }

        edit(meat_objs.cooked_moonlight_antelope) {
            contentGroup = content.food
            param[params.food_heal_value] = 14
            param[params.food_secondary_heal] = 12
        }
    }
}
