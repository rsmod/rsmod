package org.rsmod.content.interfaces.skill.guides.configs

import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.comp.ComponentType

typealias guide_enums = SkillGuideEnums

object SkillGuideEnums : EnumReferences() {
    val open_buttons = find<ComponentType, Int>("skill_guide_button_vars")
    val subsection_buttons = find<ComponentType, Int>("skill_guide_section_vars")
}

internal object SkillGuideEnumBuilder : EnumBuilder() {
    init {
        build<ComponentType, Int>("skill_guide_button_vars") {
            this[guide_components.attack] = 1
            this[guide_components.strength] = 2
            this[guide_components.ranged] = 3
            this[guide_components.magic] = 4
            this[guide_components.defence] = 5
            this[guide_components.hitpoints] = 6
            this[guide_components.prayer] = 7
            this[guide_components.agility] = 8
            this[guide_components.herblore] = 9
            this[guide_components.thieving] = 10
            this[guide_components.crafting] = 11
            this[guide_components.runecraft] = 12
            this[guide_components.mining] = 13
            this[guide_components.smithing] = 14
            this[guide_components.fishing] = 15
            this[guide_components.cooking] = 16
            this[guide_components.firemaking] = 17
            this[guide_components.woodcutting] = 18
            this[guide_components.fletching] = 19
            this[guide_components.slayer] = 20
            this[guide_components.farming] = 21
            this[guide_components.construction] = 22
            this[guide_components.hunter] = 23
        }

        build<ComponentType, Int>("skill_guide_section_vars") {
            this[guide_components.subsection_1] = 0
            this[guide_components.subsection_2] = 1
            this[guide_components.subsection_3] = 2
            this[guide_components.subsection_4] = 3
            this[guide_components.subsection_5] = 4
            this[guide_components.subsection_6] = 5
            this[guide_components.subsection_7] = 6
            this[guide_components.subsection_8] = 7
            this[guide_components.subsection_9] = 8
            this[guide_components.subsection_10] = 9
            this[guide_components.subsection_11] = 10
            this[guide_components.subsection_12] = 11
            this[guide_components.subsection_13] = 12
            this[guide_components.subsection_14] = 13
        }
    }
}
