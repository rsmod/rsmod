package org.rsmod.content.skills.woodcutting.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.obj.ObjEditor

internal object WoodcuttingAxes : ObjEditor() {
    init {
        edit(objs.bronze_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_bronze_axe
        }

        edit(objs.iron_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_iron_axe
        }

        edit(objs.steel_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_steel_axe
        }

        edit(objs.black_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_black_axe
        }

        edit(objs.mithril_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_mithril_axe
        }

        edit(objs.adamant_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_adamant_axe
        }

        edit(objs.blessed_axe) {
            param[params.skill_anim] = seqs.human_woodcutting_blessed_axe
            // Blessed axe does not have its `levelrequire` param defined by default.
            param[params.levelrequire] = 35
        }

        edit(objs.rune_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_rune_axe
        }

        edit(objs.gilded_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_gilded_axe
        }

        edit(objs.dragon_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_dragon_axe
        }

        edit(objs.dragon_axe_or) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_trailblazer_axe_no_infernal
        }

        edit(objs.third_age_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_3a_axe
        }

        edit(objs.infernal_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_infernal_axe
        }

        edit(objs.infernal_axe_or) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_trailblazer_axe
        }

        edit(objs.crystal_axe) {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_crystal_axe
        }
    }
}
