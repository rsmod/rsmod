package org.runerealm.content.interfaces.journal.tab

import org.runerealm.api.utils.vars.VarEnumDelegate

enum class SideJournalTab(override val varValue: Int) : VarEnumDelegate {
    Summary(varValue = 0),
    Quests(varValue = 1),
    Tasks(varValue = 2),
}
