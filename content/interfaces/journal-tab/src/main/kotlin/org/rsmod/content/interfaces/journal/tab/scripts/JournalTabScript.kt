package org.rsmod.content.interfaces.journal.tab.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.content.interfaces.journal.tab.SideJournalTab
import org.rsmod.content.interfaces.journal.tab.configs.journal_components
import org.rsmod.content.interfaces.journal.tab.openJournalTab
import org.rsmod.content.interfaces.journal.tab.sideJournalTab
import org.rsmod.content.interfaces.journal.tab.switchJournalTab
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class JournalTabScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    override fun ScriptContext.startUp() {
        onIfOpen(interfaces.side_journal) { player.openActiveJournal() }

        onIfOverlayButton(journal_components.summary_list) {
            player.switchJournalTab(SideJournalTab.Summary)
        }

        onIfOverlayButton(journal_components.quest_list) {
            player.switchJournalTab(SideJournalTab.Quests)
        }

        onIfOverlayButton(journal_components.task_list) {
            player.switchJournalTab(SideJournalTab.Tasks)
        }
    }

    private fun Player.openActiveJournal() = openJournalTab(sideJournalTab, eventBus)

    private fun Player.switchJournalTab(open: SideJournalTab) = switchJournalTab(open, eventBus)
}
