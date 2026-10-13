package org.runerealm.api.invtx

import jakarta.inject.Inject
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

// @see [docs/quirks.md] for details on why this is done.
internal lateinit var cachedInventoryTransactions: InvTransactions

public class InvTransactionsScript @Inject constructor(private val objTypes: ObjTypeList) :
    PluginScript() {
    public lateinit var transactions: InvTransactions

    override fun ScriptContext.startup() {
        val create = InvTransactions.from(objTypes)
        transactions = create
        cachedInventoryTransactions = create
    }
}
