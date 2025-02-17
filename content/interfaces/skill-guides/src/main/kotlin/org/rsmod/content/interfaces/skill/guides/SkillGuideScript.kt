package org.rsmod.content.interfaces.skill.guides

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.vars.intVarBit
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
        val mappedTabButtons = enumResolver[guide_enums.open_buttons].filterValuesNotNull()
        for ((button, varbit) in mappedTabButtons) {
            onIfOverlayButton(button) { player.selectGuide(varbit) }
        }

        val mappedSubsections = enumResolver[guide_enums.subsection_buttons].filterValuesNotNull()
        for ((button, varbit) in mappedSubsections) {
            onIfOverlayButton(button) { player.changeSubsection(varbit) }
        }

        onIfOverlayButton(guide_components.close_button) { player.closeGuide() }
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

private var Player.selectedSkill by intVarBit(guide_varbits.selected_skill)
private var Player.selectedSubsection by intVarBit(guide_varbits.selected_subsection)
