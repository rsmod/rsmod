package org.rsmod.content.interfaces.journal.tab

import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.content.interfaces.journal.tab.configs.journal_components
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player

internal var Player.sideJournalTab by enumVarBit<SideJournalTab>(varbits.side_journal_tab)

internal fun Player.openJournalTab(tab: SideJournalTab, eventBus: EventBus) =
    when (tab) {
        SideJournalTab.Summary -> openSummaryTab(eventBus)
        SideJournalTab.Quests -> openQuestTab(eventBus)
        SideJournalTab.Tasks -> openTaskTab(eventBus)
    }

internal fun Player.openSummaryTab(eventBus: EventBus) {
    updateSummaryTimePlayed()
    updateSummaryCombatLevel()
    ifOpenOverlay(interfaces.account_summary_sidepanel, journal_components.tab_container, eventBus)
}

internal fun Player.updateSummaryTimePlayed() {
    val minutesPlayed = vars[varps.playtime] / 100
    runClientScript(
        3970,
        journal_components.summary_contents.packed,
        journal_components.summary_click_layer.packed,
        minutesPlayed,
    )
}

internal fun Player.updateSummaryCombatLevel() {
    runClientScript(
        3954,
        journal_components.summary_contents.packed,
        journal_components.summary_click_layer.packed,
        combatLevel,
    )
}

internal fun Player.openQuestTab(eventBus: EventBus) {
    ifOpenOverlay(interfaces.questlist, journal_components.tab_container, eventBus)
}

internal fun Player.openTaskTab(eventBus: EventBus) {
    ifOpenOverlay(interfaces.area_task, journal_components.tab_container, eventBus)
}

internal fun Player.prepareJournalTab(tab: SideJournalTab) =
    when (tab) {
        SideJournalTab.Summary -> prepareSummaryTab()
        SideJournalTab.Quests -> prepareQuestTab()
        SideJournalTab.Tasks -> {}
    }

internal fun Player.prepareSummaryTab() {
    resyncVar(varps.collection_count_other_max)
    resyncVar(varps.collection_count_other)
    resyncVar(varps.collection_count_minigames_max)
    resyncVar(varps.collection_count_minigames)
    resyncVar(varps.collection_count_clues_max)
    resyncVar(varps.collection_count_clues)
    resyncVar(varps.collection_count_raids_max)
    resyncVar(varps.collection_count_raids)
    resyncVar(varps.collection_count_bosses_max)
    resyncVar(varps.collection_count_bosses)
    resyncVar(varps.collection_count_max)
    resyncVar(varps.collection_count)
}

internal fun Player.prepareQuestTab() {
    ClientScripts.playerMember(this)
}

internal fun Player.closeJournalTab(tab: SideJournalTab, eventBus: EventBus) =
    when (tab) {
        SideJournalTab.Summary -> ifCloseSub(interfaces.account_summary_sidepanel, eventBus)
        SideJournalTab.Quests -> ifCloseSub(interfaces.questlist, eventBus)
        SideJournalTab.Tasks -> ifCloseSub(interfaces.area_task, eventBus)
    }

internal fun Player.switchJournalTab(open: SideJournalTab, eventBus: EventBus) {
    val previous = sideJournalTab
    sideJournalTab = open
    closeJournalTab(previous, eventBus)
    prepareJournalTab(open)
    openJournalTab(open, eventBus)
}
