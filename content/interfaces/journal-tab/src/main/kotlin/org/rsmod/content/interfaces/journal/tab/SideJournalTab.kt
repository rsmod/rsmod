package org.rsmod.content.interfaces.journal.tab

import org.rsmod.api.utils.vars.VarEnumDelegate

enum class SideJournalTab(override val varValue: Int) : VarEnumDelegate {
    Summary(varValue = 0),
    Quests(varValue = 1),
    Tasks(varValue = 2),
}
