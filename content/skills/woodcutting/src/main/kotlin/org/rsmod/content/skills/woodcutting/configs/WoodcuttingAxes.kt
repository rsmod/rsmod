package org.rsmod.content.skills.woodcutting.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.obj.ObjEditor

internal object WoodcuttingAxes : ObjEditor() {
    init {
        edit("bronze_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_bronze_axe
        }

        edit("iron_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_iron_axe
        }

        edit("steel_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_steel_axe
        }

        edit("black_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_black_axe
        }

        edit("mithril_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_mithril_axe
        }

        edit("adamant_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_adamant_axe
        }

        edit("blessed_axe") {
            param[params.skill_anim] = seqs.human_woodcutting_blessed_axe
            // Blessed axe does not have its `levelrequire` param defined by default.
            param[params.levelrequire] = 35
        }

        edit("rune_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_rune_axe
        }

        edit("gilded_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_gilded_axe
        }

        edit("dragon_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_dragon_axe
        }

        edit("dragon_axe_or") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_trailblazer_axe_no_infernal
        }

        edit("3rd_age_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_3a_axe
        }

        edit("infernal_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_infernal_axe
        }

        edit("infernal_axe_or") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_trailblazer_axe
        }

        edit("crystal_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_crystal_axe
        }
    }
}
