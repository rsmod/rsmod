package org.rsmod.content.skills.woodcutting.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.refs.param.ParamReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

object WoodcuttingParams : ParamReferences() {
    val success_rates = find<EnumType<ObjType, Int>>("woodcutting_axe_success_rates")
}

internal object WoodcuttingParamBuilder : ParamBuilder() {
    init {
        build<EnumType<ObjType, Int>>("woodcutting_axe_success_rates")
    }
}

internal object WoodcuttingEnums : EnumReferences() {
    val regular_tree_axes = find<ObjType, Int>("regular_tree_axes")
    val oak_tree_axes = find<ObjType, Int>("oak_tree_axes")
    val willow_tree_axes = find<ObjType, Int>("willow_tree_axes")
    val teak_tree_axes = find<ObjType, Int>("teak_tree_axes")
    val maple_tree_axes = find<ObjType, Int>("maple_tree_axes")
    val arctic_tree_axes = find<ObjType, Int>("arctic_tree_axes")
    val mahogany_tree_axes = find<ObjType, Int>("mahogany_tree_axes")
    val yew_tree_axes = find<ObjType, Int>("yew_tree_axes")
    val magic_tree_axes = find<ObjType, Int>("magic_tree_axes")
    val redwood_tree_axes = find<ObjType, Int>("redwood_tree_axes")
    val hollow_tree_axes = find<ObjType, Int>("hollow_tree_axes")
}

internal object WoodcuttingEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, Int>("regular_tree_axes") {
            this[objs.bronze_axe] = rate(64, 200)
            this[objs.iron_axe] = rate(96, 300)
            this[objs.steel_axe] = rate(128, 400)
            this[objs.black_axe] = rate(144, 450)
            this[objs.mithril_axe] = rate(160, 500)
            this[objs.adamant_axe] = rate(192, 600)
            this[objs.rune_axe] = rate(224, 700)
            this[objs.gilded_axe] = rate(224, 700)
            this[objs.dragon_axe] = rate(240, 750)
            this[objs.dragon_axe_or] = rate(240, 750)
            this[objs.third_age_axe] = rate(240, 750)
            this[objs.infernal_axe] = rate(240, 750)
            this[objs.infernal_axe_or] = rate(240, 750)
            this[objs.crystal_axe] = rate(250, 800)
        }

        build<ObjType, Int>("oak_tree_axes") {
            this[objs.bronze_axe] = rate(32, 100)
            this[objs.iron_axe] = rate(48, 150)
            this[objs.steel_axe] = rate(64, 200)
            this[objs.black_axe] = rate(72, 225)
            this[objs.mithril_axe] = rate(80, 250)
            this[objs.adamant_axe] = rate(96, 300)
            this[objs.rune_axe] = rate(112, 350)
            this[objs.gilded_axe] = rate(112, 350)
            this[objs.dragon_axe] = rate(120, 375)
            this[objs.dragon_axe_or] = rate(120, 375)
            this[objs.third_age_axe] = rate(120, 375)
            this[objs.infernal_axe] = rate(120, 375)
            this[objs.infernal_axe_or] = rate(120, 375)
            this[objs.crystal_axe] = rate(120, 375)
        }

        build<ObjType, Int>("willow_tree_axes") {
            this[objs.bronze_axe] = rate(16, 50)
            this[objs.iron_axe] = rate(24, 75)
            this[objs.steel_axe] = rate(32, 100)
            this[objs.black_axe] = rate(36, 112)
            this[objs.mithril_axe] = rate(40, 125)
            this[objs.adamant_axe] = rate(48, 150)
            this[objs.rune_axe] = rate(56, 175)
            this[objs.gilded_axe] = rate(56, 175)
            this[objs.dragon_axe] = rate(60, 187)
            this[objs.dragon_axe_or] = rate(60, 187)
            this[objs.third_age_axe] = rate(60, 187)
            this[objs.infernal_axe] = rate(60, 187)
            this[objs.infernal_axe_or] = rate(60, 187)
            this[objs.crystal_axe] = rate(60, 187)
        }

        build<ObjType, Int>("teak_tree_axes") {
            this[objs.bronze_axe] = rate(15, 46)
            this[objs.iron_axe] = rate(23, 70)
            this[objs.steel_axe] = rate(31, 93)
            this[objs.black_axe] = rate(35, 102)
            this[objs.mithril_axe] = rate(39, 117)
            this[objs.adamant_axe] = rate(47, 140)
            this[objs.rune_axe] = rate(55, 164)
            this[objs.gilded_axe] = rate(55, 164)
            this[objs.dragon_axe] = rate(60, 190)
            this[objs.dragon_axe_or] = rate(60, 190)
            this[objs.third_age_axe] = rate(60, 190)
            this[objs.infernal_axe] = rate(60, 190)
            this[objs.infernal_axe_or] = rate(60, 190)
            this[objs.crystal_axe] = rate(60, 200)
        }

        build<ObjType, Int>("maple_tree_axes") {
            this[objs.bronze_axe] = rate(8, 25)
            this[objs.iron_axe] = rate(12, 37)
            this[objs.steel_axe] = rate(16, 50)
            this[objs.black_axe] = rate(18, 56)
            this[objs.mithril_axe] = rate(20, 62)
            this[objs.adamant_axe] = rate(24, 75)
            this[objs.rune_axe] = rate(28, 87)
            this[objs.gilded_axe] = rate(28, 87)
            this[objs.dragon_axe] = rate(30, 93)
            this[objs.dragon_axe_or] = rate(30, 93)
            this[objs.third_age_axe] = rate(30, 93)
            this[objs.infernal_axe] = rate(30, 93)
            this[objs.infernal_axe_or] = rate(30, 93)
            this[objs.crystal_axe] = rate(30, 93)
        }

        build<ObjType, Int>("arctic_tree_axes") {
            this[objs.bronze_axe] = rate(6, 30)
            this[objs.iron_axe] = rate(8, 44)
            this[objs.steel_axe] = rate(11, 60)
            this[objs.black_axe] = rate(13, 67)
            this[objs.mithril_axe] = rate(14, 74)
            this[objs.adamant_axe] = rate(17, 90)
            this[objs.rune_axe] = rate(20, 104)
            this[objs.gilded_axe] = rate(20, 104)
            this[objs.dragon_axe] = rate(21, 112)
            this[objs.dragon_axe_or] = rate(21, 112)
            this[objs.third_age_axe] = rate(21, 112)
            this[objs.infernal_axe] = rate(21, 112)
            this[objs.infernal_axe_or] = rate(21, 112)
            this[objs.crystal_axe] = rate(21, 112)
        }

        build<ObjType, Int>("mahogany_tree_axes") {
            this[objs.bronze_axe] = rate(8, 25)
            this[objs.iron_axe] = rate(12, 38)
            this[objs.steel_axe] = rate(16, 50)
            this[objs.black_axe] = rate(18, 54)
            this[objs.mithril_axe] = rate(20, 63)
            this[objs.adamant_axe] = rate(25, 75)
            this[objs.rune_axe] = rate(29, 88)
            this[objs.gilded_axe] = rate(29, 88)
            this[objs.dragon_axe] = rate(34, 94)
            this[objs.dragon_axe_or] = rate(34, 94)
            this[objs.third_age_axe] = rate(34, 94)
            this[objs.infernal_axe] = rate(34, 94)
            this[objs.infernal_axe_or] = rate(34, 94)
            this[objs.crystal_axe] = rate(36, 97)
        }

        build<ObjType, Int>("yew_tree_axes") {
            this[objs.bronze_axe] = rate(4, 12)
            this[objs.iron_axe] = rate(6, 19)
            this[objs.steel_axe] = rate(8, 25)
            this[objs.black_axe] = rate(9, 28)
            this[objs.mithril_axe] = rate(10, 31)
            this[objs.adamant_axe] = rate(12, 37)
            this[objs.rune_axe] = rate(14, 44)
            this[objs.gilded_axe] = rate(14, 44)
            this[objs.dragon_axe] = rate(15, 47)
            this[objs.dragon_axe_or] = rate(15, 47)
            this[objs.third_age_axe] = rate(15, 47)
            this[objs.infernal_axe] = rate(15, 47)
            this[objs.infernal_axe_or] = rate(15, 47)
            this[objs.crystal_axe] = rate(15, 47)
        }

        build<ObjType, Int>("magic_tree_axes") {
            this[objs.bronze_axe] = rate(2, 6)
            this[objs.iron_axe] = rate(3, 9)
            this[objs.steel_axe] = rate(4, 12)
            this[objs.black_axe] = rate(5, 13)
            this[objs.mithril_axe] = rate(5, 15)
            this[objs.adamant_axe] = rate(6, 18)
            this[objs.rune_axe] = rate(7, 21)
            this[objs.gilded_axe] = rate(7, 21)
            this[objs.dragon_axe] = rate(7, 22)
            this[objs.dragon_axe_or] = rate(7, 22)
            this[objs.third_age_axe] = rate(7, 22)
            this[objs.infernal_axe] = rate(7, 22)
            this[objs.infernal_axe_or] = rate(7, 22)
            this[objs.crystal_axe] = rate(7, 22)
        }

        build<ObjType, Int>("redwood_tree_axes") {
            this[objs.bronze_axe] = rate(2, 6)
            this[objs.iron_axe] = rate(3, 9)
            this[objs.steel_axe] = rate(4, 12)
            this[objs.black_axe] = rate(4, 14)
            this[objs.mithril_axe] = rate(5, 15)
            this[objs.adamant_axe] = rate(6, 18)
            this[objs.rune_axe] = rate(7, 21)
            this[objs.gilded_axe] = rate(7, 21)
            this[objs.dragon_axe] = rate(7, 30)
            this[objs.dragon_axe_or] = rate(7, 30)
            this[objs.third_age_axe] = rate(7, 30)
            this[objs.infernal_axe] = rate(7, 30)
            this[objs.infernal_axe_or] = rate(7, 30)
            this[objs.crystal_axe] = rate(8, 35)
        }

        build<ObjType, Int>("hollow_tree_axes") {
            this[objs.bronze_axe] = rate(18, 26)
            this[objs.iron_axe] = rate(28, 40)
            this[objs.steel_axe] = rate(36, 54)
            this[objs.black_axe] = rate(42, 57)
            this[objs.mithril_axe] = rate(46, 68)
            this[objs.adamant_axe] = rate(59, 81)
            this[objs.rune_axe] = rate(64, 94)
            this[objs.gilded_axe] = rate(64, 94)
            this[objs.dragon_axe] = rate(67, 101)
            this[objs.dragon_axe_or] = rate(67, 101)
            this[objs.third_age_axe] = rate(67, 101)
            this[objs.infernal_axe] = rate(67, 101)
            this[objs.infernal_axe_or] = rate(67, 101)
            this[objs.crystal_axe] = rate(67, 101)
        }
    }

    private fun rate(low: Int, high: Int): Int = (low shl 16) or high
}
