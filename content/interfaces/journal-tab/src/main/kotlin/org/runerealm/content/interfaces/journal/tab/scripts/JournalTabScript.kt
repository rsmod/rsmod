package org.runerealm.content.interfaces.journal.tab.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.content.interfaces.journal.tab.SideJournalTab
import org.runerealm.content.interfaces.journal.tab.configs.journal_components
import org.runerealm.content.interfaces.journal.tab.openJournalTab
import org.runerealm.content.interfaces.journal.tab.sideJournalTab
import org.runerealm.content.interfaces.journal.tab.switchJournalTab
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class JournalTabScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    override fun ScriptContext.startup() {
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
