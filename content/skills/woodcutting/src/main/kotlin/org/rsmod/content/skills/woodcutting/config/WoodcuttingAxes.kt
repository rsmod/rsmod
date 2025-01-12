package org.rsmod.content.skills.woodcutting.config

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.obj.ObjEditor

internal object WoodcuttingAxes : ObjEditor() {
    init {
        edit("bronze_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_bronze_axe
            param[params.skill_levelreq] = 1
        }

        edit("iron_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_iron_axe
            param[params.skill_levelreq] = 1
        }

        edit("steel_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_steel_axe
            param[params.skill_levelreq] = 6
        }

        edit("black_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_black_axe
            param[params.skill_levelreq] = 11
        }

        edit("mithril_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_mithril_axe
            param[params.skill_levelreq] = 21
        }

        edit("adamant_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_adamant_axe
            param[params.skill_levelreq] = 31
        }

        edit("blessed_axe") {
            param[params.skill_anim] = seqs.human_woodcutting_blessed_axe
            param[params.skill_levelreq] = 35
        }

        edit("rune_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_rune_axe
            param[params.skill_levelreq] = 41
        }

        edit("gilded_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_gilded_axe
            param[params.skill_levelreq] = 41
        }

        edit("dragon_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_dragon_axe
            param[params.skill_levelreq] = 61
        }

        edit("dragon_axe_or") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_dragon_axe_or
            param[params.skill_levelreq] = 61
        }

        edit("3rd_age_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_3a_axe
            param[params.skill_levelreq] = 61
        }

        edit("infernal_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_infernal_axe
            param[params.skill_levelreq] = 61
        }

        edit("infernal_axe_or") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_infernal_axe_or
            param[params.skill_levelreq] = 61
        }

        edit("crystal_axe") {
            contentGroup = content.woodcutting_axe
            param[params.skill_anim] = seqs.human_woodcutting_crystal_axe
            param[params.skill_levelreq] = 71
        }
    }
}
