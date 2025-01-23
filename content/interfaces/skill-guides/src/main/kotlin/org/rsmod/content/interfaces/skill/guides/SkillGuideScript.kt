package org.rsmod.content.interfaces.skill.guides

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.content.interfaces.skill.guides.configs.guide_components
import org.rsmod.content.interfaces.skill.guides.configs.guide_enums
import org.rsmod.content.interfaces.skill.guides.configs.guide_interfaces
import org.rsmod.content.interfaces.skill.guides.configs.guide_varbits
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SkillGuideScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val enumResolver: EnumTypeMapResolver,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        val mappedComponents = enumResolver[guide_enums.open_buttons].filterValuesNotNull()
        for ((button, varbit) in mappedComponents) {
            onIfOverlayButton(button) { player.selectGuide(varbit) }
        }
        onIfOverlayButton(guide_components.close_button) { player.closeGuide() }

        onIfOverlayButton(guide_components.subsection_1) { player.changeSubsection(0) }
        onIfOverlayButton(guide_components.subsection_2) { player.changeSubsection(1) }
        onIfOverlayButton(guide_components.subsection_3) { player.changeSubsection(2) }
        onIfOverlayButton(guide_components.subsection_4) { player.changeSubsection(3) }
        onIfOverlayButton(guide_components.subsection_5) { player.changeSubsection(4) }
        onIfOverlayButton(guide_components.subsection_6) { player.changeSubsection(5) }
        onIfOverlayButton(guide_components.subsection_7) { player.changeSubsection(6) }
        onIfOverlayButton(guide_components.subsection_8) { player.changeSubsection(7) }
        onIfOverlayButton(guide_components.subsection_9) { player.changeSubsection(8) }
        onIfOverlayButton(guide_components.subsection_10) { player.changeSubsection(9) }
        onIfOverlayButton(guide_components.subsection_11) { player.changeSubsection(10) }
        onIfOverlayButton(guide_components.subsection_12) { player.changeSubsection(11) }
        onIfOverlayButton(guide_components.subsection_13) { player.changeSubsection(12) }
        onIfOverlayButton(guide_components.subsection_14) { player.changeSubsection(13) }
    }

    private fun Player.selectGuide(guideVarBit: Int) {
        ifClose(eventBus)
        protectedAccess.launch(this) { openGuide(guideVarBit, sectionVar = 0) }
    }

    private fun Player.openGuide(skillVar: Int, sectionVar: Int) {
        selectedSkill = skillVar
        selectedSubsection = sectionVar
        ifOpenOverlay(guide_interfaces.skill_guide, eventBus)
        // Note: This is for the "Check _" left-click options on subsection entries. As of the
        // moment of writing this, only construction handles this server-side. (Magic handles it
        // entirely through cs2) We do not currently have the construction data in order to send
        // the corresponding message for these ops, so we stick to never enabling them.
        ifSetEvents(guide_components.subsection_entry_list, 0..99)
    }

    private fun Player.changeSubsection(sectionVar: Int) {
        openGuide(selectedSkill, sectionVar)
    }

    private fun Player.closeGuide() {
        ifCloseSub(guide_interfaces.skill_guide, eventBus)
    }
}

private var Player.selectedSkill by intVarp(guide_varbits.selected_skill)
private var Player.selectedSubsection by intVarp(guide_varbits.selected_subsection)
