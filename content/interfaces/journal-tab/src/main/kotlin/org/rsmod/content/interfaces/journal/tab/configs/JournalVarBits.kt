package org.rsmod.content.interfaces.journal.tab.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias journal_varbits = JournalVarBits

object JournalVarBits : VarBitReferences() {
    val display_playtime_remind_disable =
        find("account_summary_display_playtime_remind_disable", 161980929182190)
    val display_playtime = find("account_summary_display_playtime", 161980929178379)
}
