package org.runerealm.content.interfaces.journal.tab.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.player.ui.ifOpenOverlay
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.content.interfaces.journal.tab.SideJournalTab
import org.runerealm.content.interfaces.journal.tab.configs.journal_components
import org.runerealm.content.interfaces.journal.tab.configs.journal_varbits
import org.runerealm.content.interfaces.journal.tab.switchJournalTab
import org.runerealm.content.interfaces.journal.tab.updateSummaryTimePlayed
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SummarySideScript
@Inject
constructor(private val eventBus: EventBus, private val protectedAccess: ProtectedAccessLauncher) :
    PluginScript() {
    private var ProtectedAccess.displayPlaytime by boolVarBit(journal_varbits.display_playtime)
    private var ProtectedAccess.displayPlaytimeReminderDisabled by
        boolVarBit(journal_varbits.display_playtime_remind_disable)

    override fun ScriptContext.startup() {
        onIfOpen(interfaces.account_summary_sidepanel) { player.onSummarySideOpen() }
        onIfOverlayButton(journal_components.summary_click_layer) {
            player.clickSummaryLayer(comsub)
        }
    }

    private fun Player.onSummarySideOpen() {
        ifSetEvents(
            journal_components.summary_click_layer,
            3..7,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
        )
    }

    private fun Player.clickSummaryLayer(comsub: Int) {
        when (comsub) {
            3 -> clickQuestList()
            4 -> clickAchievementList()
            5 -> clickCombatAchievements()
            6 -> clickCollectionLog()
            7 -> selectTimePlayedToggle()
            else -> throw NotImplementedError("Unhandled summary click: comsub=$comsub")
        }
    }

    private fun Player.clickQuestList() {
        switchJournalTab(SideJournalTab.Quests, eventBus)
    }

    private fun Player.clickAchievementList() {
        switchJournalTab(SideJournalTab.Tasks, eventBus)
    }

    private fun Player.clickCombatAchievements() {
        ifClose(eventBus)
        val opened = protectedAccess.launch(this) { ifOpenMainModal(interfaces.ca_overview) }
        if (!opened) {
            mes("Please finish what you're doing first.")
        }
    }

    private fun Player.clickCollectionLog() {
        ifOpenOverlay(interfaces.collection, eventBus)
    }

    private fun Player.selectTimePlayedToggle() {
        ifClose(eventBus)
        val toggled = protectedAccess.launch(this) { toggleTimePlayed() }
        if (!toggled) {
            mes("Please finish what you're doing first.")
        }
    }

    private suspend fun ProtectedAccess.toggleTimePlayed() {
        if (displayPlaytimeReminderDisabled || displayPlaytime) {
            displayPlaytime = !displayPlaytime
            player.updateSummaryTimePlayed()
            return
        }

        val option =
            choice3(
                "Yes",
                1,
                "Yes and don't ask me again",
                2,
                "No",
                3,
                title = "Are you sure you want to display your time played?",
            )

        if (option == 3) {
            return
        }

        if (option == 2) {
            displayPlaytimeReminderDisabled = true
        }
        displayPlaytime = !displayPlaytime
        player.updateSummaryTimePlayed()
    }
}
