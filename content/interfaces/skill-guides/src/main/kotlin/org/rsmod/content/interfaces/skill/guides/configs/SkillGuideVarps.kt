package org.rsmod.content.interfaces.skill.guides.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias guide_varbits = SkillGuideVarBits

object SkillGuideVarBits : VarBitReferences() {
    val selected_skill = find("skill_guide_current_skill", 992134500)
    val selected_subsection = find("skill_guide_subcategory", 992398996)
}
