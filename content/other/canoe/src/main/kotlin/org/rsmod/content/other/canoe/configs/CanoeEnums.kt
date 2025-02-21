package org.rsmod.content.other.canoe.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType

typealias canoe_enums = CanoeEnums

object CanoeEnums : EnumReferences() {
    val station_axe_rates = find<ObjType, Int>("canoe_station_axe_rates")
    val shaping_axe_rates = find<ObjType, Int>("canoe_shaping_axe_rates")
    val shaping_axe_anims = find<ObjType, SeqType>("canoe_shaping_axe_anims")
}

object CanoeEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, Int>("canoe_station_axe_rates") {
            this[objs.bronze_axe] = rate(32, 100)
            this[objs.iron_axe] = rate(48, 150)
            this[objs.steel_axe] = rate(64, 200)
            this[objs.black_axe] = rate(72, 225)
            this[objs.mithril_axe] = rate(80, 250)
            this[objs.adamant_axe] = rate(96, 300)
            this[objs.rune_axe] = rate(112, 350)
            this[objs.gilded_axe] = rate(112, 350)
            this[objs.dragon_axe] = rate(112, 350)
            this[objs.dragon_axe_or] = rate(112, 350)
            this[objs.third_age_axe] = rate(112, 350)
            this[objs.infernal_axe] = rate(112, 350)
            this[objs.infernal_axe_or] = rate(112, 350)
            this[objs.crystal_axe] = rate(112, 350)
        }

        build<ObjType, Int>("canoe_shaping_axe_rates") {
            this[objs.bronze_axe] = rate(32, 100)
            this[objs.iron_axe] = rate(48, 150)
            this[objs.steel_axe] = rate(64, 200)
            this[objs.black_axe] = rate(72, 225)
            this[objs.mithril_axe] = rate(80, 250)
            this[objs.adamant_axe] = rate(96, 300)
            this[objs.rune_axe] = rate(112, 350)
            this[objs.gilded_axe] = rate(112, 350)
            this[objs.dragon_axe] = rate(112, 350)
            this[objs.dragon_axe_or] = rate(112, 350)
            this[objs.third_age_axe] = rate(112, 350)
            this[objs.infernal_axe] = rate(112, 350)
            this[objs.infernal_axe_or] = rate(112, 350)
            this[objs.crystal_axe] = rate(112, 350)
        }

        build<ObjType, SeqType>("canoe_shaping_axe_anims") {
            this[objs.bronze_axe] = canoe_seqs.shape_bronze_axe
            this[objs.iron_axe] = canoe_seqs.shape_iron_axe
            this[objs.steel_axe] = canoe_seqs.shape_steel_axe
            this[objs.black_axe] = canoe_seqs.shape_black_axe
            this[objs.mithril_axe] = canoe_seqs.shape_mithril_axe
            this[objs.adamant_axe] = canoe_seqs.shape_adamant_axe
            this[objs.rune_axe] = canoe_seqs.shape_rune_axe
            this[objs.gilded_axe] = canoe_seqs.shape_gilded_axe
            this[objs.dragon_axe] = canoe_seqs.shape_dragon_axe
            this[objs.dragon_axe_or] = canoe_seqs.shape_dragon_axe_or
            this[objs.third_age_axe] = canoe_seqs.shape_3a_axe
            this[objs.infernal_axe] = canoe_seqs.shape_infernal_axe
            this[objs.infernal_axe_or] = canoe_seqs.shape_infernal_axe_or
            this[objs.crystal_axe] = canoe_seqs.shape_crystal_axe
        }
    }

    private fun rate(low: Int, high: Int): Int = (low shl 16) or high
}
